package com.taskmanager.TaskManager.company.dto;

import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCompanyDTO {
    private CompanyRequestDTO company;
    private UserRegisterDTO user;
}
