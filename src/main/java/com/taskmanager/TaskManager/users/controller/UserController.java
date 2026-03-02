package com.taskmanager.TaskManager.users.controller;

import com.stripe.exception.StripeException;
import com.taskmanager.TaskManager.users.dto.UserLoginDTO;
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import com.taskmanager.TaskManager.users.dto.UserResponseDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.payment.StripeService;
import com.taskmanager.TaskManager.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final StripeService stripeService;
    
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllCompanyUsers() {

        return ResponseEntity.ok(service.getAllCompanyUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegisterDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(dto));

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginDTO dto) {

        String token = service.login(dto);
        return ResponseEntity.ok(token);

    }

    @PatchMapping("/upgrade")
    public ResponseEntity<Map<String, String>> upgradeUser() throws StripeException {
        User currentUser = service.getCurrentUser();
        String checkoutUrl = stripeService.createUpgradeSession(currentUser.getEmail());

        return ResponseEntity.ok(Map.of("url", checkoutUrl));
    }

}
