package com.aics.spi;

/**
 * 工具执行（由 ai-tools 实现）。
 */
public interface ToolExecutor {

    String execute(String message);
}
