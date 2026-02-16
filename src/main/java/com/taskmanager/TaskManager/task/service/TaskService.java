package com.taskmanager.TaskManager.task.service;

import com.taskmanager.TaskManager.comentary.dto.ComentResponseDTO;
import com.taskmanager.TaskManager.comentary.entity.Comentary;
import com.taskmanager.TaskManager.comentary.service.ComentaryService;
import com.taskmanager.TaskManager.task.dto.TaskRequestDTO;
import com.taskmanager.TaskManager.task.dto.TaskResponseDTO;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.task.repository.TaskRepository;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import com.taskmanager.TaskManager.users.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository tRepository;
    private final UserRepository uRepository;
    private final UserService uService;

    public TaskService(TaskRepository tRepository, UserRepository uRepository, UserService uService) {
        this.tRepository = tRepository;
        this.uRepository = uRepository;
        this.uService = uService;
    }

    public TaskResponseDTO toResponse(Task task) {

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setResponsible(task.getResponsible());
        dto.setId(task.getId());
        dto.setBody(task.getBody());
        dto.setName(task.getName());
        dto.setComentary(task.getComentary());
        dto.setOwner(task.getOwner());
        dto.setResponsible(task.getResponsible());
        dto.setInitDate(task.getInitDate());
        dto.setFinishDate(task.getFinishDate());

        return dto;
    }

    public TaskResponseDTO getTaskById(Long taskId) {

        Task task = tRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return toResponse(task);

    }

    public TaskResponseDTO createTask(TaskRequestDTO dto) {

        Task task = new Task();
        task.setBody(dto.getBody());
        task.setName(dto.getName());
        task.setResponsible(dto.getResponsible());
        task.setInitDate(dto.getInitDate());
        task.setFinishDate(dto.getFinishDate());

        User responsible = dto.getResponsible();
        uService.addTaskToList(task, responsible);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = uRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User" + username + "not found"));

        task.setOwner(owner);

        tRepository.save(task);

        return toResponse(task);

    }

    public List<TaskResponseDTO> userTasks() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = uRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User" + username + "not found"));

        List<Task> tasks = user.getTasks();

        return tasks.stream().map(this::toResponse).toList();

    }

}
