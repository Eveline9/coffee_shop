package com.coffeeShop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MyWebAppApplication {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize( org.springframework.util.unit.DataSize.ofMegabytes(12));
        factory.setMaxRequestSize(org.springframework.util.unit.DataSize.ofMegabytes(12));
        return factory.createMultipartConfig();
    }


    public static  void main (String[] args){
        SpringApplication.run(MyWebAppApplication.class, args);
    }
}
