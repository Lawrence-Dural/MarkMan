package com.markman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MarkMan Business System — a modular monolith business-management platform
 * for Philippine SMBs. See individual module {@code package-info.java} files
 * for module boundaries (enforced by Spring Modulith).
 */
@SpringBootApplication
public class MarkManApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarkManApplication.class, args);
    }
}
