package cc.ccoder.common.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: leolztang
 * @Date: 2021/12/1
 */
public class JsonResult {
    private Object data;
    private List<String> errorMsg = new ArrayList<>();

    public JsonResult() {
    }

    public JsonResult(Object data, List<String> errorMsg) {
        this.data = data;
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(List<String> errorMsg) {
        this.errorMsg = errorMsg;
    }
}
