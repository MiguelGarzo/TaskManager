package com.taskmanager.TaskManager;


import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.subtask.dto.SubtaskRequestDTO;
import com.taskmanager.TaskManager.subtask.dto.SubtaskResponseDTO;
import com.taskmanager.TaskManager.subtask.entity.Subtask;
import com.taskmanager.TaskManager.subtask.repository.SubtaskRepository;
import com.taskmanager.TaskManager.subtask.service.SubtaskService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SubtaskServiceTest {

    @Mock
    private SubtaskRepository sRepository;
    @Mock
    private UserRepository uRepository;
    @Mock
    private TaskRepository tRepository;
    @Mock
    private TaskService tService;
    @Mock
    private UserService uService;

    @InjectMocks
    private SubtaskService sService;

    @Test
    void shouldCreateSubtasks() {
        Company company = new Company();
        company.setId(1L);

        User user = new User();
        user.setUsername("Test");
        user.setCompany(company);
        user.setEmail("test@test.com");

        Task task = new Task();
        task.setName("Test1");
        task.setId(1L);
        task.setOwner(user);
        task.setResponsible(user);

        SubtaskRequestDTO stask = new SubtaskRequestDTO();
        stask.setResponsibleUsername("Test");
        stask.setName("Stask1");
        stask.setTaskId(1L);
        stask.setBody("Body1");

        when(uService.getCurrentCompanyId()).thenReturn(1L);
        when(uRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(uRepository.findByUsernameAndCompany_Id(eq("Test"), eq(1L)))
                .thenReturn(Optional.of(user));
        when(tRepository.findByIdAndOwner_Company_Id(eq(1L), eq(1L)))
                .thenReturn(Optional.of(task));

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");
        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        SubtaskResponseDTO response = sService.createSubtask(stask);

        assertEquals("Stask1", response.getName());
        assertEquals("Test", response.getResponsibleUsername());
        assertEquals(1L, response.getTaskId());
    }

    @Test
    void shouldMarkAsCompleted() {
        Company company = new Company();
        company.setId(1L);

        User user = new User();
        user.setCompany(company);

        Subtask stask = new Subtask();
        stask.setId(1L);
        stask.setCompleted(false);

        when(uService.getCurrentCompanyId()).thenReturn(1L);
        when(sRepository.findByIdAndOwner_Company_Id(eq(1L), eq(1L)))
                .thenReturn(Optional.of(stask));

        sService.markAsCompleted(stask.getId());

        assertEquals(true, stask.getCompleted());
    }


}
