package com.aics.reactivechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 纯 Netty + WebFlux 聊天进程：与 MVC 主应用分离，独立端口（默认 8081）。
 */
@SpringBootApplication(scanBasePackages = "com.aics")
public class ReactiveChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveChatApplication.class, args);
    }
}
