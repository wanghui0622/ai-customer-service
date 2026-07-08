package com.aics.integrations.domain.order;

public record OrderDto(String orderId, String status, String carrier, String eta) {
}
