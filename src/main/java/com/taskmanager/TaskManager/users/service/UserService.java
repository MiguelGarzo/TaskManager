package com.taskmanager.TaskManager.users.service;

import com.taskmanager.TaskManager.commentary.repository.CommentaryRepository;
import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.company.repository.CompanyRepository;
import com.taskmanager.TaskManager.company.service.CompanyService;
import com.taskmanager.TaskManager.security.CustomUserDetails;
import com.taskmanager.TaskManager.security.JwtUtil;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.users.CustomUserDetailsService;
import com.taskmanager.TaskManager.users.Status;
import com.taskmanager.TaskManager.users.dto.UserLoginDTO;
import com.taskmanager.TaskManager.users.dto.UserRegisterAutDTO;
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import com.taskmanager.TaskManager.users.dto.UserResponseDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository uRepository;
    private final CompanyRepository cRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final CompanyService cService;
    private final CommentaryRepository commentaryRepository;

    public UserService (UserRepository uRepository,
                        PasswordEncoder passwordEncoder,
                        CompanyRepository cRepository,
                        JwtUtil jwtUtil,
                        CustomUserDetailsService customUserDetailsService,
                        AuthenticationManager authenticationManager,
                        CompanyService cService,
                        CommentaryRepository commentaryRepository
                        )
    {
        this.uRepository = uRepository;
        this.cRepository = cRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.cService = cService;
        this.commentaryRepository = commentaryRepository;
    }

    public UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole());

        return dto;
    }

    public Long getCurrentCompanyId() {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();

        return userDetails.getCompanyId();
    }

    private User getCurrentUser() {
        Long companyId = getCurrentCompanyId();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return uRepository.findByUsername(username, companyId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<UserResponseDTO> getAllCompanyUsers() {
        Long companyId = getCurrentCompanyId();

        return uRepository.findByCompanyId(companyId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(Status.NORMAL);

        User cUser = getCurrentUser();
        Company company = cUser.getCompany();
        user.setCompany(company);

        uRepository.save(user);
        cService.addUserToCompany(user);

        return toResponse(user);
    }

    public UserResponseDTO createAutUser(UserRegisterAutDTO autDTO) {
        User user = new User();
        user.setUsername(autDTO.getUsername());
        user.setEmail(autDTO.getEmail());
        user.setRole(autDTO.getRole());
        user.setPassword(passwordEncoder.encode(autDTO.getPassword()));
        user.setStatus(Status.NORMAL);
        user.setCompany(autDTO.getCompany());

        uRepository.save(user);
        cService.addUserToCompany(user);

        return toResponse(user);
    }

    public String login(UserLoginDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService.loadUserByUsername(dto.getEmail());

        return jwtUtil.tokenGen(userDetails);

    }

    public UserResponseDTO upgradeUser(){

        User cUser = getCurrentUser();

        //Falta el codigo de Stripe

        cUser.setStatus(Status.PREMIUM);

        return toResponse(cUser);
    }

    public void addTaskToList(Task task, User user) {
        user.getTasks().add(task);
    }

    public void deleteTaskFromList(Task task, User user) {
        user.getTasks().remove(task);
    }

}
