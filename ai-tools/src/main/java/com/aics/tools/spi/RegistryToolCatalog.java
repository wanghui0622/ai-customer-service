package com.aics.tools.spi;

import com.aics.spi.ToolCatalog;
import com.aics.tools.Tool;
import com.aics.tools.registry.ToolRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegistryToolCatalog implements ToolCatalog {

    private final ToolRegistry toolRegistry;

    public RegistryToolCatalog(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    @Override
    public List<ToolDescriptor> listTools() {
        return toolRegistry.all().stream()
                .map(this::toDescriptor)
                .toList();
    }

    private ToolDescriptor toDescriptor(Tool tool) {
        return new ToolDescriptor(tool.name(), tool.description(), tool.inputSchemaJson());
    }
}
