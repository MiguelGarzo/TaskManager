package com.taskmanager.TaskManager.company.service;

import com.taskmanager.TaskManager.company.dto.CompanyRequestDTO;
import com.taskmanager.TaskManager.company.dto.CompanyResponseDTO;
import com.taskmanager.TaskManager.company.dto.CreateCompanyDTO;
import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.company.repository.CompanyRepository;
import com.taskmanager.TaskManager.users.Role;
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class CompanyService {

    private final CompanyRepository cRepository;
    private final UserService uService;

    public CompanyService(CompanyRepository cRepository, @Lazy UserService uService) {
        this.cRepository = cRepository;
        this.uService = uService;
    }

    public CompanyResponseDTO toResponse(Company company) {

        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAdress(company.getAdress());

        return dto;
    }

    public CompanyResponseDTO getMyCompany(String companyName) {

        Company company = cRepository.findByName(companyName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No company found"));

        return toResponse(company);

    }

    public CompanyResponseDTO createCompany(CreateCompanyDTO dto) {

        if (cRepository.existsByName(dto.getCompany().getName())) {
            throw new RuntimeException("Company name already exists: " + dto.getCompany().getName());
        }

        Company company = new Company();
        company.setName(dto.getCompany().getName());
        company.setAdress(dto.getCompany().getAdress());

        cRepository.save(company);

        UserRegisterDTO user = new UserRegisterDTO();
        user.setUsername(dto.getUser().getUsername());
        user.setPassword(dto.getUser().getPassword());
        user.setEmail(dto.getUser().getEmail());
        user.setRole(Role.ADMIN);

        uService.createAutUser(user, company);

        return toResponse(company);

    }

    public void addUserToCompany(User user) {
        Company company = cRepository.findById(user.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Company does not exists"));

        company.addToList(user);
    }
}
