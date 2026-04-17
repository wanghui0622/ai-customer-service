package com.aics.prompt.factory;

import com.aics.prompt.builder.PromptBuilder;
import com.aics.prompt.config.PromptEngineProperties;
import com.aics.prompt.template.PromptTemplate;
import org.springframework.stereotype.Component;

/**
 * 按场景与版本产出已绑定模板的 {@link PromptBuilder}，调用方继续链式填写变量与上下文后 {@link PromptBuilder#build()} 得到 {@link com.aics.prompt.builder.BuiltPrompt}。
 * <p>
 * 示例：
 * <pre>{@code
 * BuiltPrompt p = promptFactory.forScenario(PromptScenario.RAG, PromptVersion.V2)
 *     .variable("question", "如何退款？")
 *     .context(retrievedChunks)
 *     .build();
 * }</pre>
 */
@Component
public class PromptFactory {

    /** 默认 Prompt 版本等引擎配置。 */
    private final PromptEngineProperties properties;

    /**
     * @param properties 绑定 {@code aics.prompt.*} 的配置属性
     */
    public PromptFactory(PromptEngineProperties properties) {
        this.properties = properties;
    }

    /**
     * 使用配置中的默认版本（{@code aics.prompt.default-version}）选择内置模板。
     *
     * @param scenario 业务场景
     * @return 已设置模板的构建器
     */
    public PromptBuilder forScenario(PromptScenario scenario) {
        return forScenario(scenario, properties.getDefaultVersion());
    }

    /**
     * 指定场景与版本，从目录中选取对应模板并包装为构建器。
     *
     * @param scenario 业务场景
     * @param version  Prompt 版本（V1/V2）
     * @return 已设置模板的构建器
     */
    public PromptBuilder forScenario(PromptScenario scenario, PromptVersion version) {
        PromptTemplate base = selectTemplate(scenario, version);
        return new PromptBuilder().template(base);
    }

    /**
     * 仅获取某场景某版本的原始模板（不做构建器封装），便于自定义扩展。
     *
     * @param scenario 业务场景
     * @param version  Prompt 版本
     * @return 目录中的 {@link PromptTemplate}
     */
    public PromptTemplate templateFor(PromptScenario scenario, PromptVersion version) {
        return selectTemplate(scenario, version);
    }

    /**
     * 根据场景与版本从 {@link DefaultPromptCatalog} 选取模板。
     */
    private static PromptTemplate selectTemplate(PromptScenario scenario, PromptVersion version) {
        return switch (scenario) {
            case CHAT -> DefaultPromptCatalog.chat(version);
            case RAG -> DefaultPromptCatalog.rag(version);
            case TOOL -> DefaultPromptCatalog.tool(version);
        };
    }
}
