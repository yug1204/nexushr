package com.nexushr.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.nexushr.performance", "com.nexushr.common"})
@EnableJpaAuditing
public class PerformanceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PerformanceServiceApplication.class, args);
    }
}
