package com.aics.reactivechat.dto;

/**
 * POST /api/chat/resume 请求体。
 */
public record ChatResumeRequest(
        String sessionId,
        String approvalToken,
        String decision
) {
}
