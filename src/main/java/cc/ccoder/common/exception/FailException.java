package cc.ccoder.common.exception;

import cc.ccoder.common.base.CodeEnum;
import cc.ccoder.common.base.CodeMessageEnum;

import java.io.Serializable;

/**
 * <p></p>
 *
 * @author congcong
 * @email cong.ccoder@gmail.com
 * @date FailException.java v1.0  2021/10/29 15:30
 */
public class FailException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String code;

    public FailException(CodeEnum code, String message) {
        super(message);
        this.code = code.getCode();
    }

    public FailException(CodeMessageEnum message) {
        super(message.getMessage());
        this.code = message.getCode();
    }

    public FailException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
