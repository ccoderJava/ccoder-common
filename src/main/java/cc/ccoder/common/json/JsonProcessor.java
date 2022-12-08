package cc.ccoder.common.json;


import cc.ccoder.common.constant.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author: congcong
 * @email: congccoder@gmail.com
 * @date: 2022/11/14 11:39
 */
@Slf4j
public class JsonProcessor {

    private JsonProcessor() {
    }

    /**
     * 原对象数据
     */
    private Object sourceObject;

    /**
     * 驼峰转换开关
     */
    private boolean camelCaseEnable = false;
    /**
     * JSON节点信息
     */
    private List<String> fieldPaths;

    public static JsonProcessor create(Object sourceObject, List<String> fieldPaths, boolean camelCaseEnable) {
        JsonProcessor jsonProcessor = new JsonProcessor();
        jsonProcessor.sourceObject = sourceObject;
        jsonProcessor.camelCaseEnable = camelCaseEnable;
        jsonProcessor.fieldPaths = fieldPaths;
        return jsonProcessor;
    }

    public static JsonProcessor create(Object sourceObject, List<String> fieldPaths) {
        return create(sourceObject, fieldPaths, false);
    }

    /**
     * 按照指定的json路径，对json对象（或json字符串）进行加密， 可加密的节点数据类型必须是字符串
     *
     * <pre>
     * 示例数据：
     * {
     *     "store":{
     *         "ext":"{\"title\":\"Sayings of the Century\",\"ext\":\"{\\\"title2\\\",\\\"Sayings of the Century\\\"}\",}",
     *         "book":[
     *             {
     *                 "price":8.95,
     *                 "title":"test1"
     *             },
     *             {
     *                 "title":"title2"
     *             },
     *             {
     *                 "desc":"Sword of Honour"
     *             }
     *         ]
     *     }
     * }
     *
     * 序号       jsonPath                 是加密成功      说明
     * 1        store.ext.title             Y           字符串节点中的加密
     * 2        store.ext.ext.title2        Y           字符串节点中的字符串节点加密（1的嵌套情况）
     * 3        store.book.title            Y           数组中的字段加密
     * 4        store.ext                   Y           加密字符串，（字符串内容是JSON string）
     * 5        store.book                  N           不能加密整个JSON对象，数据类型不符，只能加密字符串
     * 6        store.book.price            N           不能加密数字，数据类型不符，只能加密字符串
     * 7        store.desc                  N           路径不正确
     *
     * </pre>
     *
     * @return 加密后的json对象或者json字符串
     */
    public JsonResult handleJson(UnaryOperator<String> operator) {
        if (fieldPaths == null || fieldPaths.isEmpty()) {
            return new JsonResult(sourceObject, new ArrayList<>());
        }
        // 组织JsonPath
        List<JsonPath> jsonPaths = buildJsonPath(null, fieldPaths);
        // 执行加密 过程
        return processJson(sourceObject, null, jsonPaths, operator);
    }

    private JsonResult processJson(JSON json, List<JsonPath> jsonPaths, UnaryOperator<String> function) {
        JsonResult res = new JsonResult(json, new ArrayList<>());
        for (JsonPath jsonPath : jsonPaths) {
            String key = jsonPath.curPathName;
            if (json instanceof JSONObject) {
                JSONObject jObject = (JSONObject) json;
                Tuple3<Boolean, String, Object> tupleCamelCase = camelCaseForJson(jObject, key);
                if (Boolean.TRUE.equals(tupleCamelCase._1())) {
                    JsonResult tmpResult = processJson(tupleCamelCase._3(), jsonPath, jsonPath.subPaths, function);
                    jObject.put(String.valueOf(tupleCamelCase._2()), tmpResult.getData());
                    res.getErrorMsg().addAll(tmpResult.getErrorMsg());
                }
            } else if (json instanceof JSONArray) {
                JSONArray array = (JSONArray) json;
                for (int i = 0; i < array.size(); i++) {
                    JSONObject curObject = array.getJSONObject(i);
                    Tuple3<Boolean, String, Object> tupleCamelCase = camelCaseForJson(curObject, key);
                    if (Boolean.TRUE.equals(tupleCamelCase._1())) {
                        JsonResult tmpResult = processJson(tupleCamelCase._3(), jsonPath, jsonPath.subPaths, function);
                        curObject.put(String.valueOf(tupleCamelCase._2()), tmpResult.getData());
                        res.getErrorMsg().addAll(tmpResult.getErrorMsg());
                    }
                }
            }
        }
        return res;
    }

    /**
     * 根据节点逐一展开json对象并进行加密/解密
     *
     * @param object    入参 json
     * @param jsonPaths json路径
     * @return 结果
     */
    private JsonResult processJson(Object object, JsonPath curPath, List<JsonPath> jsonPaths, UnaryOperator<String> function) {
        // 如果为空，直接返回
        if (object == null) {
            return new JsonResult(object, new ArrayList<>());
        }

        // 递归
        if (object instanceof String) {
            // 如果是字符串，且已经是最内层节点，则加密
            if (jsonPaths.isEmpty()) {
                return new JsonResult(operatorApply(object.toString(), function), new ArrayList<>());
            }
            JSONValidator jsonValidator = JSONValidator.from(object.toString());
            if (JSONValidator.Type.Object == jsonValidator.getType()) {
                JSONObject jObject = JSON.parseObject(object.toString());
                JsonResult tmpRes = processJson(jObject, jsonPaths, function);
                return buildJsonStringResult(tmpRes);
            } else if (JSONValidator.Type.Array == jsonValidator.getType()) {
                JSONArray jArray = JSON.parseArray(object.toString());
                JsonResult tmpRes = processJson(jArray, jsonPaths, function);
                return buildJsonStringResult(tmpRes);
            } else {
                // 处理没有加密的场景, 将路径指向了一个非json字符串时，会遇到此情况
                String msg = String.format("Can't support type. %s", object.getClass());
                return new JsonResult(object, Collections.singletonList(curPath == null ? "" : curPath.getFullPath() + ":" + msg));
            }
        } else if (object instanceof JSON) {
            return processJson((JSON) object, jsonPaths, function);
        } else {
            // 不支持其他类型，例如：数字，bool类型。将路径指向了一个非字符串时，会遇到此情况
            String msg = String.format("Can't support type. %s", object.getClass());
            return new JsonResult(object, Collections.singletonList(curPath == null ? "" : curPath.getFullPath() + ":" + msg));
        }
    }

    private JsonResult buildJsonStringResult(JsonResult jsonData) {
        String data = JSONObject.toJSONString(jsonData.getData());
        jsonData.setData(data);
        return jsonData;
    }

    /**
     * <pre>
     *     将原始的json path字符串转换为层级表示的对象，便于后续的递归处理json数据内容,
     *     示例：
     *     输入：Arrays.asList("json.path.sub2", "json.path.sub1", "json2.path.sub2")
     *
     *     输出：
     *     [
     *     {
     *         "rootPathName":"json2",
     *         "subPaths":[
     *             {
     *                 "rootPathName":"path",
     *                 "subPaths":[
     *                     {
     *                         "rootPathName":"sub2",
     *                         "subPaths":[]
     *                     }
     *                 ]
     *             }
     *         ]
     *     },
     *     {
     *         "rootPathName":"json",
     *         "subPaths":[
     *             {
     *                 "rootPathName":"path",
     *                 "subPaths":[
     *                     {
     *                         "rootPathName":"sub2",
     *                         "subPaths":[]
     *                     },
     *                     {
     *                         "rootPathName":"sub1",
     *                         "subPaths":[]
     *                     }
     *                 ]
     *             }
     *         ]
     *     }
     * ]
     *
     * NOTE：如果jsonPath有叠加， 则按照长路径处理：例如输入[rootPath.lev1, rootPath.lev1.lev2], 则按照rootPath.lev1.lev2处理
     * </pre>
     * <p>
     * NOTE: 暂不支json key中包含点的情况，例如："rootPath.Name"."subPath.Lev1", (目前基本不存在这种情况)
     *
     * @param jsonPathStr 完整的json path字符串,例如rootPathName.subPathLev1.subPathLev2
     * @return jsonPath对象
     */
    private List<JsonPath> buildJsonPath(String parentPath, List<String> jsonPathStr) {
        return jsonPathStr.stream().filter(StringUtils::isNotBlank).collect(
                        // 将path string分割后转为Map<rootName, List<subPath>>格式的map
                        Collectors.groupingBy(s -> StringUtils.substringBefore(s, Constants.POINT), Collectors.mapping(s -> StringUtils.substringAfter(s, Constants.POINT), Collectors.toList()))).entrySet().stream()
                // 递归构造JsonPath对象
                .map(entry -> {
                    JsonPath jsonPath = new JsonPath();
                    jsonPath.setCurPathName(entry.getKey());
                    jsonPath.setParentPathName(parentPath);
                    jsonPath.setSubPaths(buildJsonPath(parentPath == null ? entry.getKey() : parentPath + Constants.POINT + entry.getKey(), entry.getValue()));
                    return jsonPath;
                }).collect(Collectors.toList());
    }

    /**
     * 处理JSON#fieldName#xxx格式配置
     *
     * @param rules JSON地址配置
     * @return 返回jsonPath地址
     */
    public List<JsonPath> buildJsonPath(List<String> rules) {
        if (rules == null || rules.isEmpty()) {
            return Collections.emptyList();
        }

        return buildJsonPath(null, rules);
    }

    /**
     * 对原值进行操作
     *
     * @param value    源值
     * @param function 操作函数
     * @return 操作后的值
     */
    private String operatorApply(String value, UnaryOperator<String> function) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return function.apply(value);
    }

    private Tuple3<Boolean, String, Object> camelCaseForJson(JSONObject o, final String key) {
        final Object result = o.get(key);
        if (result != null) {
            return Tuple.of(true, key, result);
        }
        // 开关caml=false未开启时不做驼峰适配转换
        if (Boolean.FALSE.equals(camelCaseEnable)) {
            return Tuple.of(false, null, null);
        }
        //循环key匹配 billing_lastName -> billingLastName格式需要匹配
        for (Map.Entry<String, Object> jsonEntity : o.entrySet()) {
            if (jsonEntity.getKey().equalsIgnoreCase(key)) {
                return Tuple.of(true, jsonEntity.getKey(), jsonEntity.getValue());
            }
        }
        return Tuple.of(false, null, null);
    }

    /**
     * 表示一个JSON的路径 例如rootPathName.subPathLev1.subPathLev2
     */
    private static class JsonPath {
        // 当前节点的路径名，即json字段中的key
        private String curPathName;

        // 父节点的路径， 例如当前节点为root.pathLev1.pathLev2, 则parentPathName=root.pathLev1
        // 用于追踪异常信息的
        private String parentPathName;

        // 子字段，可以有多个子字段，也可以没有子字段
        private List<JsonPath> subPaths;

        public String getFullPath() {
            return parentPathName == null ? curPathName : parentPathName + Constants.POINT + curPathName;
        }

        public String getCurPathName() {
            return curPathName;
        }

        public void setCurPathName(String curPathName) {
            this.curPathName = curPathName;
        }

        public String getParentPathName() {
            return parentPathName;
        }

        public void setParentPathName(String parentPathName) {
            this.parentPathName = parentPathName;
        }

        public List<JsonPath> getSubPaths() {
            return subPaths;
        }

        public void setSubPaths(List<JsonPath> subPaths) {
            this.subPaths = subPaths;
        }
    }

}
