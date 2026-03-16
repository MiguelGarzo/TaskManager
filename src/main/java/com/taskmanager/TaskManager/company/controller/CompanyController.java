package com.taskmanager.TaskManager.company.controller;

import com.taskmanager.TaskManager.company.dto.CompanyRequestDTO;
import com.taskmanager.TaskManager.company.dto.CompanyResponseDTO;
import com.taskmanager.TaskManager.company.dto.CreateCompanyDTO;
import com.taskmanager.TaskManager.company.service.CompanyService;
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService cService;

    public CompanyController(CompanyService cService) {
        this.cService = cService;
    }

    @GetMapping("/{companyName}")
    public ResponseEntity<CompanyResponseDTO> getMyCompany(@PathVariable String companyName) {

        return ResponseEntity.ok(cService.getMyCompany(companyName));

    }

    @PostMapping("/new")
    public ResponseEntity<CompanyResponseDTO> createCompany(@RequestBody CreateCompanyDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(cService.createCompany(dto));

    }
}
