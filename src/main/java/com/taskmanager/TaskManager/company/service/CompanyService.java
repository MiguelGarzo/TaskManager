package com.taskmanager.TaskManager.company.service;

import com.taskmanager.TaskManager.company.dto.CompanyRequestDTO;
import com.taskmanager.TaskManager.company.dto.CompanyResponseDTO;
import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.company.repository.CompanyRepository;
import com.taskmanager.TaskManager.users.Role;
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.service.UserService;
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

    public CompanyResponseDTO createCompany(CompanyRequestDTO dto) {

        if (cRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Company name already exists: " + dto.getName());
        }

        Company company = new Company();
        company.setName(dto.getName());
        company.setAdress(dto.getAdress());

        cRepository.save(company);

        UserRegisterDTO user = new UserRegisterDTO();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setEmail("admin@example.com");
        user.setRole(Role.ADMIN);
        user.setCompany(company);

        uService.createUser(user);

        return toResponse(company);

    }

    public void addUserToCompany(User user) {
        Company company = cRepository.findById(user.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company does not exists"));

        company.addToList(user);
    }

}
