package com.aics.eval;

import java.util.List;

public final class DemoData {

    public static final EvaluationCase ORDER_SHIPPING = new EvaluationCase(
            "我的订单123为什么还没有发货？",
            List.of("订单", "发货", "状态", "物流"),
            "order"
    );

    private DemoData() {
    }
}
