package com.medeat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@MapperScan("com.medeat.**.dao")
@SpringBootApplication
@EnableScheduling
public class MedeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedeatApplication.class, args);
    }

}