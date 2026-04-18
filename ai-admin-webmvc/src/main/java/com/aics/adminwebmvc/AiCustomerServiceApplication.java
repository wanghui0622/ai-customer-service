package com.aics.adminwebmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * 管理后台 / 非流式 API 的可部署单元：聚合编排与各能力模块，扫描 {@code com.aics} 根包。
 */
@SpringBootApplication(
        scanBasePackages = "com.aics",
        exclude = {UserDetailsServiceAutoConfiguration.class}
)
public class AiCustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCustomerServiceApplication.class, args);
    }
}
