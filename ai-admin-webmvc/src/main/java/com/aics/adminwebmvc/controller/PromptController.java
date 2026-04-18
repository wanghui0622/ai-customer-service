package com.aics.adminwebmvc.controller;

import com.aics.adminwebmvc.dto.PromptListResponse;
import com.aics.adminwebmvc.dto.PromptSaveRequest;
import com.aics.adminwebmvc.dto.PromptItemDto;
import com.aics.adminwebmvc.service.PromptAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompt")
public class PromptController {

    private final PromptAdminService promptAdminService;

    public PromptController(PromptAdminService promptAdminService) {
        this.promptAdminService = promptAdminService;
    }

    @GetMapping()
    public PromptListResponse list() {
        return new PromptListResponse(promptAdminService.listAll());
    }

    @PostMapping()
    public PromptItemDto save(@RequestBody PromptSaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("body 不能为空");
        }
        return promptAdminService.save(request.id(), request.scenario(), request.system(), request.user());
    }
}
