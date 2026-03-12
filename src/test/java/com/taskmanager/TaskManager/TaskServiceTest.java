package com.taskmanager.TaskManager;

import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.task.Priority;
import com.taskmanager.TaskManager.task.TaskStatus;
import com.taskmanager.TaskManager.task.dto.TaskRequestDTO;
import com.taskmanager.TaskManager.task.dto.TaskResponseDTO;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.task.repository.TaskRepository;
import com.taskmanager.TaskManager.task.service.TaskService;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import com.taskmanager.TaskManager.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository tRepository;
    @Mock
    private UserRepository uRepository;
    @Mock
    private UserService uService;
    @InjectMocks
    private TaskService tService;

    @Test
    void shouldCreateTasks() {
        Company company = new Company();
        company.setId(1L);

        User user = new User();
        user.setEmail("test@test.com");
        user.setUsername("Test");
        user.setCompany(company);

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setBody("test");
        dto.setName("Test");
        dto.setPriority(Priority.LOW);
        dto.setResponsibleUsername(user.getUsername());
        dto.setInitDate(LocalDateTime.now());
        dto.setFinishDate(LocalDateTime.now());
        dto.setStatus(TaskStatus.STARTED);

        when(uService.getCurrentCompanyId()).thenReturn(1L);
        when(uRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));
        when(uRepository.findByUsernameAndCompany_Id(eq("Test"), eq(1L)))
                .thenReturn(Optional.of(user));
        when(tRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");
        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        TaskResponseDTO task = tService.createTask(dto);

        assertEquals("Test", task.getName());
        assertEquals("test", task.getBody());
        assertEquals(Priority.LOW, task.getPriority());
        assertEquals("Test", task.getResponsibleUsername());
        assertEquals(TaskStatus.STARTED, task.getStatus());

        verify(tRepository).save(any(Task.class));
        verify(uService).addTaskToList(any(Task.class), eq(user));
    }


}
