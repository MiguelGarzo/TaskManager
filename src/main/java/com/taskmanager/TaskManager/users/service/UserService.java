package com.taskmanager.TaskManager.users.service;

import com.taskmanager.TaskManager.users.dto.UserResponseDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository uRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService (UserRepository uRepository, PasswordEncoder passwordEncoder) {
        this.uRepository = uRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());

        return dto;
    }

    public List<UserResponseDTO> getAllCompanyUsers(String username) {
        User user = uRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long companyId = user.getCompanyId();

        return uRepository.findByCompanyId(companyId).stream().map(this::toResponse).collect(Collectors.toList());
    }

}
