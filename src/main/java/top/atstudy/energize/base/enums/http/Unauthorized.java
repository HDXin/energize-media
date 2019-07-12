package top.atstudy.energize.base.enums.http;

import top.atstudy.specification.enums.http.IError401Enum;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: hdxin
 * Date: 2018-07-10
 * Time: 4:38
 */
public enum Unauthorized implements IError401Enum<Unauthorized> {

    UNAUTHORIZED(4010101, "未授权"),

    ;

    private Integer code;
    private String message;
    Unauthorized(Integer code, String message) {
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
