package cc.ccoder.base;

import java.io.Serializable;

/**
 * <p></p>
 *
 * @author chencong
 * @email  cong.ccoder@gmail.com
 * @date CodeMessageEnum.java v1.0  2021/10/29 15:25
 */
public interface CodeMessageEnum extends CodeEnum, Serializable {

    /**
     * 枚举描述
     *
     * @return 枚举描述
     */
    String getMessage();

}
