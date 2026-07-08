package com.aics.tools.builtin;

import com.aics.integrations.adapter.OrderIntegrationAdapter;
import com.aics.integrations.domain.order.OrderDto;
import com.aics.tools.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单查询：通过 {@link OrderIntegrationAdapter} 对接外部订单系统。
 */
@Component
public class OrderQueryTool implements Tool {

    public static final String NAME = "order_query";
    private static final Pattern ID = Pattern.compile("(ORD[-_]?\\d+|\\d{6,})");
    private static final ObjectMapper JSON = new ObjectMapper();

    private final OrderIntegrationAdapter orderIntegrationAdapter;

    public OrderQueryTool(OrderIntegrationAdapter orderIntegrationAdapter) {
        this.orderIntegrationAdapter = orderIntegrationAdapter;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "根据用户描述查询订单状态。";
    }

    @Override
    public String inputSchemaJson() {
        return """
                {"type":"object","properties":{"query":{"type":"string","description":"用户原话或订单号"}},"required":["query"]}
                """.trim();
    }

    @Override
    public String execute(String input) {
        try {
            OrderDto order = orderIntegrationAdapter.query(input);
            return JSON.writeValueAsString(Map.of(
                    "tool", NAME,
                    "orderId", order.orderId(),
                    "status", order.status(),
                    "carrier", order.carrier(),
                    "eta", order.eta()
            ));
        } catch (Exception e) {
            String orderId = "DEMO-001";
            Matcher m = ID.matcher(input == null ? "" : input);
            if (m.find()) {
                orderId = m.group(1).replace('_', '-');
            }
            return """
                    {"tool":"order_query","orderId":"%s","status":"已发货","carrier":"演示物流","eta":"1-2个工作日","error":"%s"}
                    """.formatted(orderId, e.getMessage()).trim();
        }
    }
}
