package com.example.coffeeShop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import jakarta.servlet.MultipartConfigElement;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        XADataSourceAutoConfiguration.class,
        SecurityAutoConfiguration.class
})

public class CoffeeShopApplication {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(org.springframework.util.unit.DataSize.ofMegabytes(12));
        factory.setMaxRequestSize(org.springframework.util.unit.DataSize.ofMegabytes(12));
        return factory.createMultipartConfig();
    }


    public static void main(String[] args) {
        SpringApplication.run(CoffeeShopApplication.class, args);
    }
}
