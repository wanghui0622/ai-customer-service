package com.aics.common.exception;

/**
 * 业务层非受检异常基类，便于统一异常处理与错误码扩展。
 */
public class BaseException extends RuntimeException {

    /**
     * @param message 错误描述
     */
    public BaseException(String message) {
        super(message);
    }
}
