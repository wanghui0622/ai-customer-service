package com.aics.adminwebmvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端健康检查等轻量接口，后续可扩展配置管理、Prompt 运维等。
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    /**
     * 简单存活探测，供负载均衡或运维探针使用。
     *
     * @return 固定标识字符串
     */
    @GetMapping("/health")
    public String health() {
        return "admin-ok";
    }
}
