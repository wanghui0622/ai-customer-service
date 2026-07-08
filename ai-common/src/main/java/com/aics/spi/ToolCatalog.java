package com.aics.spi;

import java.util.List;

/**
 * 可用工具清单，供图编排 ReAct 规划节点列举工具。
 */
public interface ToolCatalog {

    List<ToolDescriptor> listTools();

    record ToolDescriptor(String name, String description, String inputSchemaJson) {
        public ToolDescriptor {
            name = name == null ? "" : name.trim();
            description = description == null ? "" : description.trim();
            inputSchemaJson = inputSchemaJson == null ? "{}" : inputSchemaJson.trim();
        }
    }
}
