package com.aics.tools.builtin;

import com.aics.tools.Tool;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模拟订单查询：未接真实订单系统，返回固定字段 JSON 字符串。
 */
@Component
public class OrderQueryTool implements Tool {

    public static final String NAME = "order_query";
    private static final Pattern ID = Pattern.compile("(ORD[-_]?\\d+|\\d{6,})");

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "根据用户描述查询订单状态（演示数据）。";
    }

    @Override
    public String inputSchemaJson() {
        return """
                {"type":"object","properties":{"query":{"type":"string","description":"用户原话或订单号"}},"required":["query"]}
                """.trim();
    }

    @Override
    public String execute(String input) {
        String orderId = "DEMO-001";
        Matcher m = ID.matcher(input == null ? "" : input);
        if (m.find()) {
            orderId = m.group(1).replace('_', '-');
        }
        return """
                {"tool":"order_query","orderId":"%s","status":"已发货","carrier":"演示物流","eta":"1-2个工作日"}
                """.formatted(orderId).trim();
    }
}
