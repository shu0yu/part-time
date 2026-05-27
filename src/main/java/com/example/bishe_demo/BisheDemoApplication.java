package com.example.bishe_demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(title = "接口文档", version = "1.0版本", description = "api接口文档")
)
class BisheDemoApplication {

    public static void main(String[] args) {SpringApplication.run(BisheDemoApplication.class, args);}

}