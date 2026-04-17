package com.aics.tools.builtin;

import com.aics.tools.Tool;
import org.springframework.stereotype.Component;

/**
 * 回显输入，用于联调与单测。
 */
@Component
public class EchoTool implements Tool {

    public static final String NAME = "echo";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "原样返回输入内容；消息以 echo: 前缀触发（演示路由）。";
    }

    @Override
    public String inputSchemaJson() {
        return """
                {"type":"object","properties":{"text":{"type":"string"}},"required":["text"]}
                """.trim();
    }

    @Override
    public String execute(String input) {
        return "[echo] " + (input == null ? "" : input);
    }
}
