package com.roadto500.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;

@SpringBootApplication
@DependsOn("flyway")
public class RoadTo500Application {
    public static void main(String[] args) {
        SpringApplication.run(RoadTo500Application.class, args);
    }
}