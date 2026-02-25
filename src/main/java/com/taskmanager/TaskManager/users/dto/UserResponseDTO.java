package com.taskmanager.TaskManager.users.dto;

import com.taskmanager.TaskManager.users.Role;
import com.taskmanager.TaskManager.users.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private String username;
    private String email;
    private Status status;
    private Role role;

}
