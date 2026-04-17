package com.aics.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 客服应用启动类：仅负责聚合各模块并扫描 {@code com.aics} 根包。
 */
@SpringBootApplication(scanBasePackages = "com.aics")
public class AiCustomerServiceApplication {

    /**
     * 应用入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AiCustomerServiceApplication.class, args);
    }
}
