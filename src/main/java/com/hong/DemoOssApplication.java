package com.hong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DemoOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoOssApplication.class, args);
    }

}
