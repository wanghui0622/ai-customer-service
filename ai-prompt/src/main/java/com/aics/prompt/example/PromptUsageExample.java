package com.aics.prompt.example;

import com.aics.prompt.builder.BuiltPrompt;
import com.aics.prompt.builder.PromptBuilder;
import com.aics.prompt.evaluator.PromptEvaluationService;
import com.aics.prompt.evaluator.PromptLog;
import com.aics.prompt.factory.PromptFactory;
import com.aics.prompt.factory.PromptScenario;
import com.aics.prompt.factory.PromptVersion;
import com.aics.prompt.template.PromptTemplate;
import java.util.List;
import java.util.Map;

/**
 * 演示 Prompt 模块的典型用法（可在单元测试或临时 Bean 中调用）。
 * <p>
 * 配置示例（application.yml）：
 * <pre>
 * aics:
 *   prompt:
 *     default-version: V2
 *     log-retention-max: 2000
 * </pre>
 */
public final class PromptUsageExample {

    private PromptUsageExample() {
    }

    /**
     * 演示：工厂选择 RAG + V2 模板，填充变量与上下文后构建，并写入评估日志。
     *
     * @param factory            Prompt 工厂
     * @param evaluationService  内存评估服务
     */
    public static void demonstrate(PromptFactory factory, PromptEvaluationService evaluationService) {
        BuiltPrompt rag = factory.forScenario(PromptScenario.RAG, PromptVersion.V2)
                .variable("question", "如何修改收货地址？")
                .context("[1] 订单发货前可在订单详情页修改地址。\n[2] 发货后需联系物流拦截。")
                .instruction("回答需引用编号。")
                .build();

        String mockAnswer = "根据 [1]，发货前您可以在订单详情页修改收货地址。";
        evaluationService.record(PromptScenario.RAG, PromptVersion.V2, rag, mockAnswer);

        List<PromptLog> tail = evaluationService.recent(10);
        if (tail.isEmpty()) {
            throw new IllegalStateException("expected at least one prompt log");
        }
    }

    /**
     * 不经过工厂，纯模板 + 构建器链式拼接。
     *
     * @return 构建好的 {@link BuiltPrompt}
     */
    public static BuiltPrompt manualTemplate() {
        PromptTemplate tpl = PromptTemplate.of(
                "你是助手。",
                "你好，{userName}。",
                "");
        return new PromptBuilder()
                .template(tpl)
                .variables(Map.of("userName", "张三"))
                .history("用户: 上次问过物流\n助手: 已为您查询。")
                .instruction("请总结下一步操作。")
                .build();
    }
}
