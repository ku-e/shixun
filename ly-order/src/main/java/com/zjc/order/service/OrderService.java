package com.zjc.order.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjc.common.enums.ExceptionEnum;
import com.zjc.common.exception.LyException;
import com.zjc.common.util.IdWorker;
import com.zjc.order.client.AddressClient;
import com.zjc.order.client.GoodsClient;
import com.zjc.order.config.LoginInterceptor;
import com.zjc.order.dto.AddressDTO;
import com.zjc.order.dto.OrderDto;
import com.zjc.order.dto.OrderStatusEnum;
import com.zjc.order.mapper.OrderDetailMapper;
import com.zjc.order.mapper.OrderMapper;
import com.zjc.order.mapper.OrderStatusMapper;
import com.zjc.order.pojo.Order;
import com.zjc.order.pojo.OrderDetail;
import com.zjc.order.pojo.OrderStatus;
import com.zjc.order.pojo.PageResult;
import com.zjc.order.utils.PayHelper;
import com.zjc.shop.pojo.Sku;
import com.zjc.shop.pojo.Stock;
import com.zjc.shop.pojo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PayHelper payHelper;

    @Autowired
    private PayLogService payLogService;

    private static final String KEY_PREFIX = "ly:cart:uid:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Transactional
    public Long createOrder(OrderDto orderDto) {
        //生成订单ID，采用自己的算法生成订单ID
        long orderId = idWorker.nextId();

        //填充order，订单中的用户信息数据从Token中获取，填充到order中
        Order order = new Order();
        order.setCreateTime(new Date());
        order.setOrderId(orderId);
        order.setPaymentType(orderDto.getPaymentType());// 付款类型
        order.setPostFee(0L);  //// TODO 调用物流信息，根据地址计算邮费

        //获取用户信息
        UserInfo user = LoginInterceptor.getLoginUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);  //卖家为留言

        //收货人地址信息，应该从数据库中物流信息中获取，这里使用的是假的数据
        AddressDTO addressDTO = AddressClient.findById(orderDto.getAddressId());
        if (addressDTO == null) {
            // 商品不存在，抛出异常
            throw new LyException(ExceptionEnum.RECEIVER_ADDRESS_NOT_FOUND);
        }
        order.setReceiver(addressDTO.getName());//收件人姓名
        order.setReceiverAddress(addressDTO.getAddress());//收件人地址
        order.setReceiverCity(addressDTO.getCity());//收件人城市
        order.setReceiverDistrict(addressDTO.getDistrict());//收件人区域
        order.setReceiverMobile(addressDTO.getPhone());//收件人电话
        order.setReceiverZip(addressDTO.getZipCode());//邮政编码
        order.setReceiverState(addressDTO.getState());//收件人省份


        //付款金额相关，首先把orderDto转化成map，其中key为skuId,值为购物车中该sku的购买数量
        Map<Long, Integer> skuNumMap = orderDto.getOrderDetails().stream().collect(Collectors.toMap(c -> c.getSkuId(), c -> c.getNum()));

        //查询商品信息，根据skuIds批量查询sku详情
        List<Sku> skus = goodsClient.queryskusByIds(new ArrayList<>(skuNumMap.keySet()));
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        Double totalPay = 0.0;

        //填充orderDetail
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        //遍历skus，填充orderDetail
        for (Sku sku : skus) {
            Integer num = skuNumMap.get(sku.getId());
            Stock stock = goodsClient.queryStockBySkuId(sku.getId());
            //购买数量是不是超过判断库存
            if(num > stock.getStock()){
                throw new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }
            totalPay += num * sku.getPrice();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            orderDetail.setNum(num);
            orderDetail.setPrice(sku.getPrice().longValue());
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));

            orderDetails.add(orderDetail);
        }
        //实付金额 = 商品总金额 + 邮费 - 优惠金额
        order.setActualPay((totalPay.longValue() + order.getPostFee()));  //todo 还要减去优惠金额
        //订单总金额
        order.setTotalPay(totalPay.longValue());

        //保存order
        orderMapper.insertSelective(order);

        //保存detail
        orderDetailMapper.insertList(orderDetails);


        //填充orderStatus
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
        orderStatus.setCreateTime(new Date());

        //保存orderStatus
        orderStatusMapper.insertSelective(orderStatus);

        //减库存
        goodsClient.decreaseStock(orderDto.getOrderDetails());

        //清空购物车
        String key = KEY_PREFIX + user.getId();
        redisTemplate.delete(key);

        return orderId;

    }

    public String generateUrl(Long orderId){
        //根据订单ID查询订单
        Order order = queryById(orderId);
        //判断订单状态
        if (order.getOrderStatus().getStatus() != OrderStatusEnum.INIT.value()){
            throw  new LyException(ExceptionEnum.ORDER_STATUS_EXCEPTION);
        }

        String url = payHelper.createPayUrl(orderId,"安明商城测试", order.getActualPay());
        if (StringUtils.isBlank(url)){
            throw  new LyException(ExceptionEnum.CREATE_PAY_URL_ERROR);
        }

        //生成支付日志
        payLogService.createPayLog(orderId, order.getActualPay());

        return url;

    }

    public Order queryById(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
        order.setOrderDetails(orderDetails);
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        order.setOrderStatus(orderStatus);
        return order;
    }


    @Transactional
    public void handleNotify(Map<String, String> msg) {
        payHelper.handleNotify(msg);
    }

    public PageResult<Order> queryOrderByPage(Integer page, Integer rows) {

        //开启分页
        PageHelper.startPage(page, rows);

        Example example = new Example(Order.class);

        //查询订单
        List<Order> orders = orderMapper.selectByExample(example);


        //查询订单详情
        for (Order order : orders) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getOrderId());
            List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetail);

            order.setOrderDetails(orderDetailList);

            //查询订单状态
            OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(order.getOrderId());
            order.setOrderStatus(orderStatus);
        }

        PageInfo<Order> pageInfo = new PageInfo<>(orders);

        Integer pages = pageInfo.getPages();
        return new PageResult<Order>(pageInfo.getTotal(), pages, pageInfo.getList());
    }
}
