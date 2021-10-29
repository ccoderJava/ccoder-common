package cc.ccoder.common.exception.fail;

import cc.ccoder.common.base.CommonResponseCode;
import cc.ccoder.common.exception.FailException;

/**
 * <p>
 * 参数校验异常
 * </p>
 *
 * @author chencong
 * @email cong.chen@payby.com | cong.ccoder@gmail.com
 * @date InvalidParameterException.java v1.0 2021/6/28 20:23
 */
public class InvalidParameterException extends FailException {

    public InvalidParameterException(String message) {
        super(CommonResponseCode.INVALID_PARAMETER, message);
    }

}