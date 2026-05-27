package com.example.bishe_demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI springShopOpenApi() {
        return new OpenAPI()
                // 接口文档标题
                .info(new Info()
                        .title("兼职管理系统接口文档")
                        .description("本接口文档包含了兼职管理系统的所有API接口，包括用户管理、企业管理、学生管理、兼职岗位管理、申请管理和聊天管理等功能。")
                        .version("1.0版本")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")
                        )
                );
    }
}
