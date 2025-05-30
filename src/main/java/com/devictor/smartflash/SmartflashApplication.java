package com.devictor.smartflash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SmartflashApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartflashApplication.class, args);
    }

}
