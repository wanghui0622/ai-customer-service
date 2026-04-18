package com.aics.adminwebmvc.controller;

import com.aics.adminwebmvc.dto.MemorySessionsResponse;
import com.aics.memory.store.MemoryStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会话记忆管理（依赖 {@link MemoryStore#listSessionIds()}，仅部分实现可用）。
 */
@RestController
@RequestMapping("/api/memory")
public class MemoryAdminController {

    private final MemoryStore memoryStore;

    public MemoryAdminController(MemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    @GetMapping("/sessions")
    public MemorySessionsResponse sessions() {
        return new MemorySessionsResponse(memoryStore.listSessionIds());
    }
}
