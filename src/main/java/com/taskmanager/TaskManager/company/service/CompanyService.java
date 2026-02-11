package com.taskmanager.TaskManager.company.service;

import com.taskmanager.TaskManager.company.dto.CompanyRequestDTO;
import com.taskmanager.TaskManager.company.dto.CompanyResponseDTO;
import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.company.repository.CompanyRepository;
import com.taskmanager.TaskManager.users.entity.User;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository cRepository;

    public CompanyService(CompanyRepository cRepository) {
        this.cRepository = cRepository;
    }

    public CompanyResponseDTO toResponse(Company company) {

        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());

        return dto;
    }

    public CompanyResponseDTO getMyCompany(String companyName) {

        Company company = cRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("No company found"));

        return toResponse(company);

    }

    public CompanyResponseDTO createCompany(CompanyRequestDTO dto) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setAdress(dto.getAdress());

        cRepository.save(company);
        return toResponse(company);
    }

}
