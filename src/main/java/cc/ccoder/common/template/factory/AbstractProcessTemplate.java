package cc.ccoder.common.template.factory;

import cc.ccoder.common.base.CommonResponseCode;
import cc.ccoder.common.exception.ErrorException;
import cc.ccoder.common.exception.FailException;
import cc.ccoder.common.request.AbstractRequest;
import cc.ccoder.common.response.CommonResponse;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * </p>
 *
 * @author congcong
 * @email cong.ccoder@gmail.com
 * @date AbstractProcessorTemplate.java v1.0 2021/6/28 20:37
 */
public abstract class AbstractProcessTemplate<Request extends AbstractRequest, Response extends CommonResponse> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 服务名称
     *
     * @return 服务名称
     */
    protected abstract String getServiceName();

    /**
     * 返回空的返回对象
     *
     * @return 空的响应对象
     */
    protected abstract Response createEmptyResponse();

    /**
     * 校验请求对象
     *
     * @param request 服务请求，非空
     */
    protected abstract void validate(Request request);

    /**
     * 业务处理
     *
     * @param request  服务请求，非空
     * @param response 服务响应
     */
    protected abstract void process(Request request, Response response);

    /**
     * 处理请求
     */
    public Response process(Request request) {
        StopWatch watch = StopWatch.createStarted();
        Response response = createEmptyResponse();
        try {
            beforeValidate(request);
            validate(request);
            process(request, response);
        } catch (Throwable e) {
            processException(request, e, response);
        } finally {
            watch.stop();
            afterProcess(request, response, watch);
        }
        return response;
    }

    protected void beforeValidate(Request request) {
        logRequest(request);
    }

    /**
     * 处理异常
     *
     * @param request  请求
     * @param e        异常
     * @param response 响应
     */
    protected void processException(Request request, Throwable e, Response response) {
        if (e instanceof FailException) {
            FailException fe = (FailException) e;
            response.fail(fe.getCode(), fe.getMessage());
        } else if (e instanceof ErrorException) {
            ErrorException ee = (ErrorException) e;
            logRequest(request);

            logger.error("处理异常", ee);
            response.error(ee.getCode(), ee.getMessage());
        } else {
            logRequest(request);
            logger.error("未知异常", e);
            response.error(CommonResponseCode.SYSTEM_ERROR);
        }
    }

    /**
     * 处理之后，返回结果之前
     *
     * @param request  请求
     * @param response 相应
     * @param watch    计时器
     */
    protected void afterProcess(Request request, Response response, StopWatch watch) {
        logResponse(response, watch);

    }

    /**
     * 记录请求
     *
     * @param request 请求
     */
    protected void logRequest(Request request) {
        logger.info("请求: {}, {} ", getServiceName(), request);
    }

    /**
     * 记录响应
     *
     * @param response 响应
     */
    protected void logResponse(Response response, StopWatch watch) {
        logger.info("Response(time: {} ms): {}", watch.getTime(TimeUnit.MILLISECONDS), response);
    }

}
