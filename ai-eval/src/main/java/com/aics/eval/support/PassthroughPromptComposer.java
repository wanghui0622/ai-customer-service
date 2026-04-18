package com.aics.eval.support;

import com.aics.spi.PromptComposer;

import java.util.List;

public final class PassthroughPromptComposer implements PromptComposer {
    @Override
    public String build(String history, List<String> context, String toolResult, String message) {
        return message == null ? "" : message;
    }
}
