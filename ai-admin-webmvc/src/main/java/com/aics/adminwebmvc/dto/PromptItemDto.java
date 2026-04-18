package com.aics.adminwebmvc.dto;

public record PromptItemDto(
        String id,
        String scenario,
        String version,
        String system,
        String user,
        boolean builtin
) {
}
