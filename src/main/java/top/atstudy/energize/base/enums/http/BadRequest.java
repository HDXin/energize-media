package top.atstudy.energize.base.enums.http;


import top.atstudy.specification.enums.http.IError400Enum;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: hdxin
 * Date: 2018-07-10
 * Time: 3:38
 */
public enum BadRequest implements IError400Enum<BadRequest> {

    USER_LOGIN_AUTH_FAILED(4010101, "登陆授权失败，账号或密码错误"),
    PARAMS_INVALID(4000201, "请求参数不合法"),
    QR_CODE_CREATE_FAIL(4000301, "二维码创建失败"),

    ADMIN_USER_ID_INVALID(4000401, "userId 无效"),
    ADMIN_USER_OLD_PASS_INVALID(4000402, "输入密码错误"),
    ADMIN_USER_NAME_NULL(4000403, "用户名不能为空"),
    ADMIN_USER_NAME_EXISTS(4000504, "用户名已存在"),
    ADMIN_USER_PASSWORD_NULL(4000605, "密码不能为空"),
    ADMIN_USER_NAME_NOT_EXISTS(4000607, "该用户不存在"),
    ADMIN_USER_NAME_OR_PASS_INVALID(4000608, "用户名或密码不正确"),

    ARTICLE_CODE_IS_NULL(4000701, "文章code不能为空"),
    ARTICLE_CODE_IS_EXISTS(4000702, "文章code已存在"),

    APP_USER_JSCODE_IS_NULL(4000801, "jscode不能为空"),
    APP_USER_JSCODE_INVALID(4000802, "jscode不正确"),
    APP_USER_ID_INVALID(4000803, "当前用户不存在"),

    SETTING_KEY_INVALID(4000901, "系统配置KEY不能为空"),

    FAVORITE_RELATION_ID_INVALID(4001001, "关联ID不能为空"),
    FAVORITE_RELATION_TYPE_INVALID(4001002, "收藏类型不能为空"),
    FAVORITE_INFO_NOT_EXISTS(4001003, "指定的收藏信息不存在"),

    ORDER_INFO_ORDERNO_IS_NULL(4001101, "订单号不能为空"),
    ORDER_INFO_NOT_EXISTS(4001102, "订单不存在"),
    ORDER_INFO_STATUS_INVALID(4001103, "订单暂不能支付"),

    LEVEL_ID_IS_NULL(4001201, "购买项ID不能为空"),
    LEVEL_ID_INVALID(4001202, "购买项ID无效"),

    ;

    private Integer code;
    private String message;
    BadRequest(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

}
