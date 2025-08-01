package com.org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication(scanBasePackages = "com.org")
public class OnlineJobPortal1122Application {
    public static void main(String[] args) {
        SpringApplication.run(OnlineJobPortal1122Application.class, args);
    }
}