package com.zjc.user.controller;

import com.zjc.common.enums.ExceptionEnum;
import com.zjc.common.exception.LyException;
import com.zjc.common.util.CookieUtils;
import com.zjc.shop.pojo.User;
import com.zjc.shop.pojo.UserInfo;
import com.zjc.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "user:code:phone:";


    /**
     *校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data, @PathVariable(value = "type") Integer type) {
        Boolean boo = this.userService.checkData(data, type);
        if (boo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(boo);
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("send")
    public ResponseEntity<Void> sendVerifyCode(String phone){
        Boolean boo = this.userService.sendVerifyCode(phone);
        if (boo == null || !boo) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code){


        /*String key =KEY_PREFIX + user.getPhone();
        //从redis去除验证码
        String codeCache = this.redisTemplate.opsForValue().get(key);
        //判断前台传来的验证码和redis比较
        if(!code.equals(codeCache)){
            return ResponseEntity.status(HttpStatus.PROXY_AUTHENTICATION_REQUIRED).build();
        }*/

        Boolean boo = this.userService.register(user,code);
        if(boo==null || !boo){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        //获取浏览器传过来的token值
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
        System.out.println("==============login=============="+token);
        if(token != null){
            //验证token值是不是在redis当中
            User user = userService.getUserByRedis(token);
            if(user != null){
                return ResponseEntity.ok().build();
            }
        }
        //登陆操作
        token = userService.queryUser(username, password);
        if (StringUtils.isBlank(token)) {
            throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        //将Token写入cookie中
        CookieUtils.setCookie(request, response, "LY_TOKEN",
                token, 1800, null, true);
        return ResponseEntity.ok().build();
    }

    /**
     * 验证用户信息
     * @param token
     * @param request
     * @param response
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN")String token,
                                               HttpServletRequest request,
                                               HttpServletResponse response){
        System.out.println("============verify=========="+token);

        try {
            //从Token中获取用户信息
            User user =userService.getUserByRedis(token);
            if(user==null){
                System.out.println("没有找到用户");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            UserInfo userInfo = new UserInfo(user.getId(),user.getUsername());
            //重新添加到redis中
            userService.addUserByRedis(token,user);
            //将newToken写入cookie中
            CookieUtils.setCookie(request, response, "LY_TOKEN",
                    token, 1800, null, true);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            //Token无效
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * 注销登录
     *
     * @param token
     * @param request
     * @param response
     * @return
     */
    @GetMapping("Logout")
    public Boolean logout(@CookieValue("LY_TOKEN") String token,HttpServletRequest request,HttpServletResponse response) {
        //String token = request.getParameter("LY_TOKEN");
        try {
            if (StringUtils.isNotBlank(token)) {
                CookieUtils.setCookie(request, response, "LY_TOKEN",
                        token, 0, null, true);
                userService.deleteUserByRedis(token);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
