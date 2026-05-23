package com.nexushr.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * NexusHR AI Workforce Intelligence Service.
 * Provides predictive attrition modeling, skill gap analysis,
 * engagement scoring, and AI-powered HR policy Q&A.
 */
@SpringBootApplication(scanBasePackages = {"com.nexushr.ai", "com.nexushr.common"})
@EnableScheduling
public class AiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
    }
}
