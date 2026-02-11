package com.taskmanager.TaskManager.users.dto;

import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.users.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {

    private String email;
    private String username;
    private String password;
    private Company company;
    private Role role;

}
