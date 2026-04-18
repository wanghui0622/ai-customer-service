package com.aics.adminwebmvc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RagAddRequest(String title, String content) {
}
