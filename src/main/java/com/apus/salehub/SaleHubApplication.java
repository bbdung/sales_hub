package com.apus.salehub;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {"com.apus.salehub", "com.apus.base", "com.apus.common"}
)
@SecurityScheme(name = "Authorization", scheme = "basic", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(
        info = @Info(title = "Sale Hub API", version = "1.0"),
        servers = {
                @Server(url = "/services/sale-hub", description = "Micro Server URL"),
                @Server(url = "/", description = "Default Server URL")
        },
        security = {@SecurityRequirement(name = "Authorization")}
)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com")
@EnableCaching
public class SaleHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaleHubApplication.class, args);
    }
}
