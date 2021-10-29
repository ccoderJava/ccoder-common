package cc.ccoder.common;

import cc.ccoder.base.CodeMessageEnum;

/**
 * <p></p>
 *
 * @author chencong
 * @email cong.ccoder@gmail.com
 * @date ApplyStatusEnum.java v1.0  2021/10/29 15:23
 */
public enum ApplyStatusEnum implements CodeMessageEnum {

    /** 申请成功 */
    SUCCESS("S", "申请成功"),

    /** 申请失败 */
    FAIL("F", "申请失败"),

    /** 申请异常，结果不确定 */
    ERROR("E", "申请异常"),

    ;

    public static ApplyStatusEnum getCode(String code) {
        for (ApplyStatusEnum type : ApplyStatusEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }


    private final String code;
    private final String message;

    ApplyStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
