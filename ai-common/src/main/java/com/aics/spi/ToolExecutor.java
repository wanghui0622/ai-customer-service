package com.aics.spi;

/**
 * 工具执行（由 ai-tools 实现）。
 */
public interface ToolExecutor {

    String execute(String message);

    /**
     * 按显式工具名执行；默认回退到 {@link #execute(String)}（启发式路由）。
     *
     * @param toolName 可为空或空白，表示由实现自行从 message 中解析
     */
    default String executeNamed(String toolName, String message) {
        return execute(message);
    }
}
