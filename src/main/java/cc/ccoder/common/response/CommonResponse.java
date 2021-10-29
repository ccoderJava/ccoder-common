package cc.ccoder.common.response;

import cc.ccoder.common.ApplyStatusEnum;
import cc.ccoder.common.base.CodeEnum;
import cc.ccoder.common.base.CodeMessageEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * <p></p>
 *
 * @author congcong
 * @email cong.ccoder@gmail.com
 * @date CommonResponse.java v1.0  2021/10/29 16:30
 */
public class CommonResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 是否成功 */
    protected ApplyStatusEnum applyStatus;

    /** 返回码 */
    protected String code;

    /** 返回信息 */
    protected String message;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


    public CommonResponse code(String code) {
        this.code = code;
        return this;
    }

    public CommonResponse message(String message) {
        this.message = message;
        return this;
    }

    public CommonResponse codeMessage(CodeMessageEnum codeMessage) {
        this.message = codeMessage.getMessage();
        this.code = codeMessage.getCode();
        return this;
    }

    public CommonResponse codeMessage(CodeEnum codeEnum, String message) {
        this.code = codeEnum.getCode();
        this.message = message;
        return this;
    }

    public CommonResponse success() {
        this.applyStatus = ApplyStatusEnum.SUCCESS;
        return this;
    }

    public CommonResponse success(CodeMessageEnum codeMessage) {
        return success().codeMessage(codeMessage);
    }

    public CommonResponse success(CodeEnum code, String message) {
        return success().codeMessage(code, message);
    }

    public CommonResponse success(String code, String message) {
        return success().code(code).message(message);
    }

    public CommonResponse success(String message) {
        return success().message(message);
    }


    protected CommonResponse fail() {
        this.applyStatus = ApplyStatusEnum.FAIL;
        return this;
    }

    public CommonResponse fail(CodeMessageEnum codeMessage) {
        return fail().codeMessage(codeMessage);
    }

    public CommonResponse fail(CodeEnum code, String message) {
        return fail().codeMessage(code, message);
    }

    public CommonResponse fail(String code, String message) {
        return fail().code(code).message(message);
    }


    protected CommonResponse error() {
        this.applyStatus = ApplyStatusEnum.ERROR;
        return this;
    }

    public CommonResponse error(CodeMessageEnum codeMessage) {
        return error().codeMessage(codeMessage);
    }

    public CommonResponse error(CodeEnum code, String message) {
        return error().codeMessage(code, message);
    }

    public CommonResponse error(String code, String message) {
        return error().code(code).message(message);
    }


    public static CommonResponse buildSuccess() {
        return new CommonResponse().success();
    }

    public static CommonResponse buildSuccess(CodeMessageEnum codeMessage) {
        return new CommonResponse().success(codeMessage);
    }

    public static CommonResponse buildSuccess(CodeEnum code, String message) {
        return new CommonResponse().success(code, message);
    }

    public static CommonResponse buildSuccess(String code, String message) {
        return new CommonResponse().success(code, message);
    }

    public static CommonResponse buildSuccess(String message) {
        return new CommonResponse().success(message);
    }


    public static CommonResponse buildFail(CodeMessageEnum codeMessage) {
        return new CommonResponse().fail(codeMessage);
    }

    public static CommonResponse buildFail(CodeEnum code, String message) {
        return new CommonResponse().fail(code, message);
    }

    public static CommonResponse buildFail(String code, String message) {
        return new CommonResponse().fail(code, message);
    }

    public static CommonResponse buildError(CodeMessageEnum codeMessage) {
        return new CommonResponse().error(codeMessage);
    }

    public static CommonResponse buildError(CodeEnum code, String message) {
        return new CommonResponse().error(code, message);
    }

    public static CommonResponse buildError(String code, String message) {
        return new CommonResponse().error(code, message);
    }

    public ApplyStatusEnum getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(ApplyStatusEnum applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
