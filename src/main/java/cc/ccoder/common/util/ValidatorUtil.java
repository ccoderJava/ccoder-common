package cc.ccoder.common.util;

import cc.ccoder.common.exception.fail.InvalidParameterException;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * <p>
 * 校验工具类
 * </p>
 *
 * @author congcong
 * @email cong.ccoder@gmail.com
 * @date ValidatorUtil.java v1.0 2021/6/28 22:11
 */
public class ValidatorUtil {

    private static final Validator VALIDATOR;

    static {
        HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class).configure()
                .addProperty("hibernate.validator.fail_fast", "true");

        ConstraintMapping constraintMapping = configuration.createConstraintMapping();

        VALIDATOR = configuration.addMapping(constraintMapping).buildValidatorFactory().getValidator();
    }

    /**
     * 验证一个对象，错误信息自动加上错误字段名称
     *
     * @param <T> 参数类型
     * @param o   请求参数
     * @throws InvalidParameterException
     */
    public static <T> void validate(T o) throws IllegalArgumentException {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(o);
        if (violations != null && violations.size() > 0) {
            ConstraintViolation<T> violation = violations.iterator().next();
            throw new InvalidParameterException(violation.getMessage() + ": " + violation.getPropertyPath());
        }
    }

    /**
     * 验证一个对象，错误信息不带字段路径
     *
     * @param <T> 参数类型
     * @param o   请求参数
     * @throws InvalidParameterException
     */
    public static <T> void validateNoPath(T o) throws IllegalArgumentException {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(o);
        if (violations != null && violations.size() > 0) {
            ConstraintViolation<T> violation = violations.iterator().next();
            throw new InvalidParameterException(violation.getMessage());
        }
    }

}
