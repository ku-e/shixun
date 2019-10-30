package com.zjc.user.service;

import com.zjc.common.enums.ExceptionEnum;
import com.zjc.common.exception.LyException;
import com.zjc.common.util.CodecUtils;
import com.zjc.common.util.JsonUtils;
import com.zjc.common.util.NumberUtils;
import com.zjc.shop.pojo.User;
import com.zjc.user.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    static final String KEY_PREFIX = "user:code:phone:";

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(user)==0;
    }

    /**
            * 生成验证码 往手机上发送
     * @param phone
     */
    public Boolean sendVerifyCode(String phone) {
        //生成验证码
        String code = NumberUtils.generateCode(6);
        try {
            //生成短信
            Map<String,String> msg = new HashMap<>();
            msg.put("phone",phone);
            msg.put("code",code);

            this.amqpTemplate.convertAndSend("sms.exchange","sms.verify.code",msg);
            //将code存入redis

            this.redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            return true;
        }catch (Exception e){
            logger.error("发送短信失败。phone:{},code:{}",phone,code);
            return false;
        }
    }


    public Boolean register(User user, String code) {
        String key =KEY_PREFIX + user.getPhone();
        //从redis获取验证码
        String codeCache = this.redisTemplate.opsForValue().get(key);
        System.out.println(codeCache);
        //检查验证码是否正确
        if(!code.equals(codeCache)){
            //不正确,返回
            return false;
        }
        user.setId(null);
        user.setCreated(new Date());
        //生成盐
        String salt = CodecUtils.generateSalt();
        //对密码加密
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //写入数据库
        boolean boo=this.userMapper.insertSelective(user) ==1;
        //如果注册成功,删除redis中的code
        if(boo){
            try {
                this.redisTemplate.delete(key);
            }catch (Exception e){
                logger.error("删除缓存验证码失败，code:{}",code,e);
            }
        }
        return boo;
    }

    public User getUserByRedis(String token) {
        String userString = redisTemplate.opsForValue().get(token);
        if(StringUtils.isBlank(userString)){
            return null;
        }
        return JsonUtils.toBean(userString, User.class);

    }

    public String queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);

        //首先根据用户名查询用户
        User user = userMapper.selectOne(record);

        if (user == null) {
            throw new LyException(ExceptionEnum.USER_NOT_EXIST);
        }

        //检验密码是否正确
        if (!StringUtils.equals(CodecUtils.md5Hex(password, user.getSalt()), user.getPassword())) {
            //密码不正确
            throw new LyException(ExceptionEnum.PASSWORD_NOT_MATCHING);
        }
        String token = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(token,JsonUtils.toString(user),1800,TimeUnit.SECONDS);
        return token;
    }


    public String addUserByRedis(String token, User user) {
        redisTemplate.opsForValue().set(token,JsonUtils.toString(user),1800,TimeUnit.SECONDS);
        return token;
    }

    public void deleteUserByRedis(String token) {
        redisTemplate.delete(token);
    }
}
