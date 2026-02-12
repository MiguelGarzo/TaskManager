package com.taskmanager.TaskManager.users.service;

import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.company.repository.CompanyRepository;
import com.taskmanager.TaskManager.company.service.CompanyService;
import com.taskmanager.TaskManager.security.CustomUserDetails;
import com.taskmanager.TaskManager.security.JwtUtil;
import com.taskmanager.TaskManager.users.CustomUserDetailsService;
import com.taskmanager.TaskManager.users.Status;
import com.taskmanager.TaskManager.users.dto.UserLoginDTO;
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import com.taskmanager.TaskManager.users.dto.UserResponseDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository uRepository;
    private final CompanyRepository cRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final CompanyService cService;

    public UserService (UserRepository uRepository,
                        PasswordEncoder passwordEncoder,
                        CompanyRepository cRepository,
                        JwtUtil jwtUtil,
                        CustomUserDetailsService customUserDetailsService,
                        AuthenticationManager authenticationManager,
                        CompanyService cService)
    {
        this.uRepository = uRepository;
        this.cRepository = cRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.cService = cService;
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

        Long companyId = user.getCompany().getId();

        return uRepository.findByCompanyId(companyId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(Status.NORMAL);

        Company company = cRepository.findById(dto.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        user.setCompany(company);

        uRepository.save(user);

        cService.addUserToCompany(user);

        return toResponse(user);
    }

    public String login(UserLoginDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService.loadUserByUsername(dto.getUsername());

        return jwtUtil.tokenGen(userDetails);

    }

}
