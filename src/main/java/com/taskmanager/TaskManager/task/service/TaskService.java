package com.taskmanager.TaskManager.task.service;

import com.taskmanager.TaskManager.commentary.entity.Commentary;
import com.taskmanager.TaskManager.commentary.repository.CommentaryRepository;
import com.taskmanager.TaskManager.subtask.entity.Subtask;
import com.taskmanager.TaskManager.subtask.repository.SubtaskRepository;
import com.taskmanager.TaskManager.task.dto.TaskRequestDTO;
import com.taskmanager.TaskManager.task.dto.TaskResponseDTO;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.task.repository.TaskRepository;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import com.taskmanager.TaskManager.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository tRepository;
    private final UserRepository uRepository;
    private final UserService uService;
    private final SubtaskRepository sRepository;
    private final CommentaryRepository cRepository;

    public TaskService(TaskRepository tRepository,
                       UserRepository uRepository,
                       UserService uService,
                       SubtaskRepository sRepository,
                       CommentaryRepository cRepository)
    {
        this.tRepository = tRepository;
        this.uRepository = uRepository;
        this.uService = uService;
        this.sRepository = sRepository;
        this.cRepository = cRepository;
    }

    public TaskResponseDTO toResponse(Task task) {

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setBody(task.getBody());
        dto.setName(task.getName());
        dto.setCommentary(task.getCommentary());
        dto.setOwnerUsername(task.getOwner().getUsername());
        dto.setResponsibleUsername(task.getResponsible().getUsername());
        dto.setInitDate(task.getInitDate());
        dto.setFinishDate(task.getFinishDate());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());

        return dto;
    }

    public TaskResponseDTO getTaskById(Long taskId) {

        Task task = tRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        return toResponse(task);

    }

    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        Task task = new Task();
        task.setBody(dto.getBody());
        task.setName(dto.getName());
        task.setInitDate(dto.getInitDate());
        task.setFinishDate(dto.getFinishDate());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        User responsible = uRepository.findByUsername(dto.getResponsibleUsername())
                        .orElseThrow(() -> new RuntimeException("User" + dto.getResponsibleUsername() + "not found"));
        task.setResponsible(responsible);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Username: " + username);
        User owner = uRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User " + username + " not found"));

        task.setOwner(owner);

        tRepository.save(task);
        uService.addTaskToList(task, responsible);

        return toResponse(task);

    }

    public List<TaskResponseDTO> userTasks() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = uRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User" + username + "not found"));

        List<Task> tasks = user.getTasks();

        return tasks.stream().map(this::toResponse).toList();

    }

    public List<TaskResponseDTO> tasksByUser(String username) {
        User user = uRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User" + username + "not found"));

        List<Task> tasks = user.getTasks();

        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskResponseDTO editTask(Long taskId, TaskRequestDTO dto) {
        Task task = tRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task" + taskId + "not found"));

        User previousResponsible = task.getResponsible();
        uService.deleteTaskFromList(task, previousResponsible);

        task.setBody(dto.getBody());
        task.setName(dto.getName());
        task.setInitDate(dto.getInitDate());
        task.setFinishDate(dto.getFinishDate());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        User responsible = uRepository.findByUsername(dto.getResponsibleUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setResponsible(responsible);
        uService.addTaskToList(task, responsible);

        return toResponse(task);

    }

    public void removeTask(Long taskId) {

        Task task = tRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task" + taskId + "not found"));

        User user = task.getResponsible();
        uService.deleteTaskFromList(task, user);

        tRepository.delete(task);

    }

    public void addSubtaskToTask(Task task, Subtask stask) {
        task.getSubtask().add(stask);
    }

    public void removeSubtaskFromTask(Task task, Subtask stask) {
        task.getSubtask().remove(stask);
    }

    public void addComentToTask(Task task, Commentary comment) {
        task.getCommentary().add(comment);
    }
}
