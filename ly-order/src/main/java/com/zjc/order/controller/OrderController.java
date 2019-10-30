package com.zjc.order.controller;


import com.zjc.order.dto.OrderDto;
import com.zjc.order.pojo.Order;
import com.zjc.order.pojo.PageResult;
import com.zjc.order.service.OrderService;
import com.zjc.order.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayLogService payLogService;

    /**
     * 创建订单
     * @param orderDto
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto orderDto){
        System.out.println("========createOrder============");
        Long orderId = orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    /**
     * 生成微信支付链接
     *
     * @param orderId
     * @return
     */
    @GetMapping("url/{id}")
    public ResponseEntity<String> generateUrl(@PathVariable("id") Long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.generateUrl(orderId));
    }

    /**
     * 根据订单ID查询订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.queryById(orderId));
    }

    /**
     * 查询订单支付状态
     * @param orderId
     * @return
     */
    @GetMapping("state/{id}")
    public ResponseEntity<Integer> queryOrderStateByOrderId(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(payLogService.queryOrderStateByOrderId(orderId));
    }

    /**
     * 分页查询所有订单
     *
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<PageResult<Order>> queryOrderByPage(@RequestParam("page") Integer page,
                                                              @RequestParam("rows") Integer rows) {

        return ResponseEntity.ok(orderService.queryOrderByPage(page, rows));
    }
}
