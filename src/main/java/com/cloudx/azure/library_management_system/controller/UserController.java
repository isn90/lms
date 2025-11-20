package com.cloudx.azure.library_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details", description = "Fetch user details by user ID")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String userId) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("userId", userId);
        userDetails.put("username", "john.doe");
        userDetails.put("email", "john.doe@example.com");
        userDetails.put("fullName", "John Doe");
        userDetails.put("role", "MEMBER");
        userDetails.put("memberSince", "2024-01-15");
        userDetails.put("booksCheckedOut", 3);
        userDetails.put("status", "ACTIVE");

        return ResponseEntity.ok(userDetails);
    }
}
