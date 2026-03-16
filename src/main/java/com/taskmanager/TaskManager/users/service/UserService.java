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
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import com.taskmanager.TaskManager.users.dto.UserResponseDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository uRepository;
    private final CompanyRepository cRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final CompanyService cService;
    private final CommentaryRepository commentaryRepository;

    public UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole());
        dto.setCurrentPeriodEnd(user.getCurrentPeriodEnd());

        return dto;
    }

    public Long getCurrentCompanyId() {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();

        return userDetails.getCompanyId();
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return uRepository.findByEmail(username)
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
        user.setStripeCustomerId(null);
        user.setStripeSubscriptionId(null);
        user.setSubscriptionStatus("NONE");
        user.setCurrentPeriodEnd(null);

        User cUser = getCurrentUser();
        Company company = cUser.getCompany();
        user.setCompany(company);

        uRepository.save(user);
        cService.addUserToCompany(user);

        return toResponse(user);
    }

    public UserResponseDTO createAutUser(UserRegisterDTO autDTO, Company company) {
        User user = new User();
        user.setUsername(autDTO.getUsername());
        user.setEmail(autDTO.getEmail());
        user.setRole(autDTO.getRole());
        user.setPassword(passwordEncoder.encode(autDTO.getPassword()));
        user.setStatus(Status.NORMAL);
        user.setCompany(company);
        user.setStripeCustomerId(null);
        user.setStripeSubscriptionId(null);
        user.setSubscriptionStatus("NONE");
        user.setCurrentPeriodEnd(null);

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

    public void activateSubscription(String email, String customerId, String subscriptionId, Long currentPeriodEnd) {
        User user = uRepository.findByEmail(email).orElseThrow();

        user.setStripeCustomerId(customerId);
        user.setStripeSubscriptionId(subscriptionId);
        user.setCurrentPeriodEnd(currentPeriodEnd);

        uRepository.save(user);
    }

    public void updateSubscription(String stripeCustomerId, String customerEmail, Long periodEnd, String status) {
        Optional<User> oUser = uRepository.findByStripeCustomerId(stripeCustomerId);

        if (oUser.isEmpty()) {

            Optional<User> userByEmail = uRepository.findByEmail(customerEmail);

            if (userByEmail.isEmpty()) {
                System.out.println("Usuario no encontrado aún para customerId: " + stripeCustomerId);
                return;
            }

            User user = userByEmail.get();
            user.setStripeCustomerId(stripeCustomerId);
            oUser = Optional.of(user);
        }

        User user = oUser.get();

        if (periodEnd != null) {
            user.setCurrentPeriodEnd(periodEnd);
        }

        user.setSubscriptionStatus(status);

        if ("ACTIVE".equals(status)) {
            user.setStatus(Status.PREMIUM);
        } else if ("PAST_DUE".equals(status)) {
            user.setStatus(Status.NORMAL);
        }
        uRepository.save(user);
    }

    public void downgradeUser(String stripeCustomerId) {
        User user = uRepository.findByStripeCustomerId(stripeCustomerId).orElseThrow();
        user.setStatus(Status.NORMAL);
        user.setSubscriptionStatus("CANCELED");
        user.setCurrentPeriodEnd(null);
        uRepository.save(user);
    }

    public void addTaskToList(Task task, User user) {
        user.getTasks().add(task);
    }

    public void deleteTaskFromList(Task task, User user) {
        user.getTasks().remove(task);
    }

}
