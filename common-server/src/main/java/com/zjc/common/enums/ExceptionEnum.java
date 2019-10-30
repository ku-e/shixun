package com.zjc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 异常的枚举
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400, "价格不能为空"),
    CATEGORY_NOT_FOUND(404, "商品分类没有找到"),
    SPEC_GROUP_NOT_FOUND(404, "商品规格组没有查到"),
    BRAND_SAVE_ERROR(500, "新增品牌失败"),
    GOODS_SAVE_ERROR(500, "新增商品失败"),
    BRAND_NOT_FOUND(404, "品牌没有找到"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    /**无效文件类型*/
    INVALID_FILE_TYPE(400, "无效文件类型"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数不存在"),
    GOODS_NOT_FOUND(404,"商品不存在"),
    SPU_DETAIL_NOT_FOUND(404, "商品详情不存在"),

    BRAND_CREATE_FAILED(500, "新增品牌失败"),
    UPDATE_BRAND_FAILED(500, "品牌更新失败"),
    DELETE_BRAND_EXCEPTION(500, "删除品牌失败"),

    GOODS_NOT_SALEABLE(400, "商品未上架"),
    GOODS_UPDATE_ERROR(500, "商品更新失败"),
    DELETE_GOODS_ERROR(500, "删除商品错误"),
    UPDATE_SALEABLE_ERROR(500, "更新商品销售状态错误"),
    STOCK_NOT_ENOUGH(500, "商品库存不足"),

    STOCK_NOT_FOUND(204, "库存查询失败"),
    SPU_NOT_FOUND(201, "SPU未查询到"),
    SKU_NOT_FOUND(201, "SKU未查询到"),

    RECEIVER_ADDRESS_NOT_FOUND(400, "收获地址不存在"),
    ORDER_NOT_FOUND(400, "订单不存在"),
    ORDER_STATUS_EXCEPTION(500, "订单状态异常"),
    CREATE_PAY_URL_ERROR(500, "常见支付链接异常"),
    WX_PAY_SIGN_INVALID(400, "微信支付签名异常"),
    WX_PAY_NOTIFY_PARAM_ERROR(400, "微信支付回调参数异常"),

    INVALID_FILE_FORMAT(400, "文件格式错误"),
    UPLOAD_IMAGE_EXCEPTION(500, "文件上传异常"),
    INVALID_PARAM(400, "参数错误"),
    USERNAME_OR_PASSWORD_ERROR(400, "账号或密码错误"),
    VERIFY_CODE_NOT_MATCHING(400, "验证码错误"),
    PASSWORD_NOT_MATCHING(400, "密码错误"),
    USER_NOT_EXIST(404, "用户不存在"),

    UPDATE_SPEC_PARAM_FAILED(500, "商品规格参数更新失败"),
    DELETE_SPEC_PARAM_FAILED(500, "商品规格参数删除失败"),
    SPEC_PARAM_CREATE_FAILED(500, "新增规格参数失败"),
    USER_NOT_LOGIN(401, "用户未登录，请登录"),

    SPEC_GROUP_CREATE_FAILED(500, "新增规格组失败"),
    DELETE_SPEC_GROUP_FAILED(500, "商品规格组删除失败"),
    UPDATE_SPEC_GROUP_FAILED(500, "商品规格组更新失败"),

    GOODS_SKU_NOT_FOUND(404,"sku没有找到");

    private int code;
    private String msg;
}