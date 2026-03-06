package com.taskmanager.TaskManager;

import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.company.service.CompanyService;
import com.taskmanager.TaskManager.users.Role;
import com.taskmanager.TaskManager.users.dto.UserRegisterDTO;
import com.taskmanager.TaskManager.users.dto.UserResponseDTO;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import com.taskmanager.TaskManager.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository uRepository;

    @Mock
    private CompanyService cService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService uService;

    @Test
    void shouldActivateSubscription() {

        User user = new User();
        user.setEmail("test@test.com");

        when(uRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        uService.activateSubscription("test@test.com", "cus_123", "sub_123", 1699999999L);

        assertEquals("cus_123", user.getStripeCustomerId());
        assertEquals("sub_123", user.getStripeSubscriptionId());
        assertEquals(1699999999L, user.getCurrentPeriodEnd());

        verify(uRepository).save(user);
    }

    @Test
    void shouldUpdateSubscription() {

        User user = new User();
        user.setStripeCustomerId("cus_123");

        when(uRepository.findByStripeCustomerId("cus_123")).thenReturn(Optional.of(user));
        uService.updateSubscription("cus_123", "test@test.com", 1699999999L, "ACTIVE");

        assertEquals("ACTIVE", user.getSubscriptionStatus());

        verify(uRepository).save(user);
    }

    @Test
    void shouldSetSubscriptionPastDue() {

        User user = new User();
        user.setStripeCustomerId("cus_123");

        when(uRepository.findByStripeCustomerId("cus_123")).thenReturn(Optional.of(user));
        uService.updateSubscription("cus_123", "test@test.com", 1699999999L, "PAST_DUE");

        assertEquals("PAST_DUE", user.getSubscriptionStatus());

        verify(uRepository).save(user);
    }

    @Test
    void shouldCreateAutUser() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("test@test.com");
        dto.setUsername("test");
        dto.setPassword("1234");
        dto.setRole(Role.ADMIN);

        Company company = new Company();
        company.setId(1L);
        company.setName("Testing");

        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        when(uRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponseDTO response = uService.createAutUser(dto, company);

        assertEquals("test", response.getUsername());
        assertEquals("test@test.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        verify(passwordEncoder).encode("1234");
        verify(uRepository).save(any(User.class));
        verify(cService).addUserToCompany(any(User.class));
    }
}
