package cc.ccoder.common.template.factory;

/**
 * <p>
 * </p>
 *
 * @author  congcong
 * @email cong.ccoder@gmail.com
 * @date CodeServiceFactory.java v1.0 2021/6/28 19:43
 */
public interface CodeServiceFactory<Provider extends CodeService> {

    /**
     * 获取服务
     * 
     * @param serviceCode
     *            服务编码
     * @return 返回服务，不存在时返回null
     */
    Provider getService(String serviceCode);
}
