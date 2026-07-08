package com.aics.prompt.composer;

import com.aics.model.ToolCallRecord;
import com.aics.spi.PromptComposer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultPromptComposer implements PromptComposer {

    @Override
    public String build(String history, List<String> context, String toolResult, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是专业 AI 客服助手，回答简洁、礼貌，使用中文。\n\n");
        if (history != null && !history.isBlank()) {
            sb.append("### 历史对话\n").append(history.trim()).append("\n\n");
        }
        if (context != null && !context.isEmpty()) {
            sb.append("### 参考知识\n");
            for (int i = 0; i < context.size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(context.get(i).trim()).append('\n');
            }
            sb.append('\n');
        }
        if (toolResult != null && !toolResult.isBlank()) {
            sb.append("### 工具结果\n").append(toolResult.trim()).append("\n\n");
        }
        sb.append("### 用户问题\n").append(message == null ? "" : message.trim());
        return sb.toString();
    }

    @Override
    public String build(String history,
                        List<String> context,
                        String toolResult,
                        String message,
                        List<ToolCallRecord> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            return build(history, context, toolResult, message);
        }
        StringBuilder toolSection = new StringBuilder();
        for (int i = 0; i < toolCalls.size(); i++) {
            ToolCallRecord call = toolCalls.get(i);
            toolSection.append('[').append(i + 1).append("] ")
                    .append(call.name()).append('\n')
                    .append(call.output()).append("\n\n");
        }
        return build(history, context, toolSection.toString().trim(), message);
    }
}
