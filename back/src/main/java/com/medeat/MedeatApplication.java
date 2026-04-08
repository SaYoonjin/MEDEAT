package com.medeat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.medeat.**.dao")
@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class MedeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedeatApplication.class, args);
    }
}
