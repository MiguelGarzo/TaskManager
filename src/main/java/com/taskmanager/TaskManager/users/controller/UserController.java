package com.taskmanager.TaskManager.users.controller;

import com.taskmanager.TaskManager.users.dto.UserResponseDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllCompanyUsers(User user) {

        String username = user.getUsername();
        return ResponseEntity.ok(service.getAllCompanyUsers(username));

    }

}
