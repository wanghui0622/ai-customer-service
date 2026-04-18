package com.aics.adminwebmvc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PromptSaveRequest(String id, String scenario, String system, String user) {
}
