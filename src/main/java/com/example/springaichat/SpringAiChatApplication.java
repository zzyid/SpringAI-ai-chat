package com.example.springaichat;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.springaichat.mapper")
@SpringBootApplication
public class SpringAiChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiChatApplication.class, args);
    }

}
