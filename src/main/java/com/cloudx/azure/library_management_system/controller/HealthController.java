package com.cloudx.azure.library_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Tag(name = "Health Check", description = "API health monitoring endpoints")
class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the API is running")
    public String health() {
        return "OK";
    }

    /*@GetMapping("/")
    @Operation(summary = "Home page", description = "API welcome page")
    public String home() {
        return "Library Management System is running! Use /lms/swagger-ui.html for API documentation";
    }*/

    @GetMapping("/")
    public void redirectRootToHealth(HttpServletResponse response) throws IOException {
        response.sendRedirect("swagger-ui/index.html");
    }
}
