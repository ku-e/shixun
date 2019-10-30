package com.zjc.ams.listener;


import com.zjc.ams.properties.SmsProperties;
import com.zjc.ams.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class SmsListener {
    private static final String SMS_PREFIX = "sms:phone:";

    @Autowired
    private StringRedisTemplate template;
    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private SmsProperties props;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.sms.verify.queue"),
            exchange = @Exchange(name = "sms.exchange", type = ExchangeTypes.TOPIC),
            key = "sms.verify.code"
    ))
    public void listenVerifyCode(Map<String, Object> msg) {
        System.out.println("=================收到短信验证码======================");
        System.out.println(msg);
        //这里可以调用短信发送接口将验证码发送给用户

        String phone = (String)msg.get("phone");
        System.out.println(phone);
        String key = SMS_PREFIX + phone;
        System.out.println(key);
        template.opsForValue().set(key,
                String.valueOf(System.currentTimeMillis()), 1,
                TimeUnit.MINUTES);
        smsUtil.sendSms(props.getSignName(), props.getVerifyCodeTemplate(), phone, msg);
    }


}
