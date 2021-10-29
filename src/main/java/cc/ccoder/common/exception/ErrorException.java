package cc.ccoder.common.exception;

import cc.ccoder.common.base.CommonResponseCode;
import cc.ccoder.common.base.CodeEnum;
import cc.ccoder.common.base.CodeMessageEnum;

/**
 * <p></p>
 *
 * @author congcong
 * @email cong.ccoder@gmail.com
 * @date ErrorException.java v1.0  2021/10/29 15:33
 */
public class ErrorException extends RuntimeException {

    private final String code;

    public ErrorException(String message) {
        this(message, CommonResponseCode.SYSTEM_ERROR);
    }

    public ErrorException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ErrorException(String message, CodeEnum code) {
        super(message);
        this.code = code.getCode();
    }

    public ErrorException(CodeMessageEnum code) {
        super(code.getMessage());
        this.code = code.getCode();
    }

    public ErrorException(Throwable e) {
        this(CommonResponseCode.SYSTEM_ERROR, e);
    }

    public ErrorException(String message, Throwable e) {
        this(CommonResponseCode.SYSTEM_ERROR, message, e);
    }

    public ErrorException(String code, String message, Throwable e) {
        super(message, e);
        this.code = code;
    }

    public ErrorException(CodeEnum code, String message, Throwable e) {
        super(message, e);
        this.code = code.getCode();
    }

    public ErrorException(CodeMessageEnum code, Throwable e) {
        super(code.getMessage(), e);
        this.code = code.getCode();
    }

    public String getCode() {
        return code;
    }
}
