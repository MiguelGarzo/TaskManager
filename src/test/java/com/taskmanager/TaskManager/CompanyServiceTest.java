package com.taskmanager.TaskManager;

import com.taskmanager.TaskManager.company.dto.CompanyRequestDTO;
import com.taskmanager.TaskManager.company.dto.CreateCompanyDTO;
import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.company.repository.CompanyRepository;
import com.taskmanager.TaskManager.company.service.CompanyService;
import com.taskmanager.TaskManager.users.Role;
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository cRepository;
    @Mock
    private UserService uService;

    @InjectMocks
    private CompanyService cService;

    @Test
    void shouldCreateACompany() {

        CompanyRequestDTO requestDTO = new CompanyRequestDTO();
        requestDTO.setName("Testing");

        UserRegisterDTO userDTO = new UserRegisterDTO();
        userDTO.setUsername("Test");
        userDTO.setRole(Role.USER);
        userDTO.setEmail("test@test.com");
        userDTO.setPassword("1234");

        CreateCompanyDTO dto = new CreateCompanyDTO();
        dto.setCompany(requestDTO);
        dto.setUser(userDTO);

        when(cRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        cService.createCompany(dto);

        assertEquals("Testing", dto.getCompany().getName());
        assertEquals("Test", dto.getUser().getUsername());
    }

    @Test
    void shouldReturnCompany() {
        Company company = new Company();
        company.setName("Testing");

        when(cRepository.findByName("Testing"))
                .thenReturn(Optional.of(company));

        Company found = cRepository.findByName("Testing").
                orElseThrow(() -> new RuntimeException("Company not found"));

        assertEquals(company.getName(), found.getName());
    }

}
