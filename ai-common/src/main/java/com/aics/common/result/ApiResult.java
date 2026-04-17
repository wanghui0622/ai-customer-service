package com.aics.common.result;

/**
 * 统一 API 响应封装。
 *
 * @param success 是否成功
 * @param message 提示信息
 * @param data    业务数据，失败时可为 {@code null}
 * @param <T>     数据类型
 */
public record ApiResult<T>(boolean success, String message, T data) {

    /**
     * 构造成功响应，消息固定为 {@code OK}。
     *
     * @param data 载荷
     * @param <T>  类型
     * @return 成功结果
     */
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(true, "OK", data);
    }

    /**
     * 构造失败响应，无业务数据。
     *
     * @param message 错误说明
     * @param <T>     类型
     * @return 失败结果
     */
    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(false, message, null);
    }
}
