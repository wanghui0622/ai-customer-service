package com.aics.adminwebmvc.controller;

import com.aics.adminwebmvc.dto.RagAddRequest;
import com.aics.adminwebmvc.dto.RagDocumentItem;
import com.aics.adminwebmvc.dto.RagListResponse;
import com.aics.adminwebmvc.service.RagAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagAdminService ragAdminService;

    public RagController(RagAdminService ragAdminService) {
        this.ragAdminService = ragAdminService;
    }

    @PostMapping("/add")
    public RagDocumentItem add(@RequestBody RagAddRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("body 不能为空");
        }
        return ragAdminService.add(request.title(), request.content());
    }

    @GetMapping("/list")
    public RagListResponse list() {
        return new RagListResponse(ragAdminService.list());
    }
}
