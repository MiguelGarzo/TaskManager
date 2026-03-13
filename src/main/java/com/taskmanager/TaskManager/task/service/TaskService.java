package com.taskmanager.TaskManager.task.service;

import com.taskmanager.TaskManager.commentary.dto.CommentResponseDTO;
import com.taskmanager.TaskManager.commentary.entity.Commentary;
import com.taskmanager.TaskManager.commentary.repository.CommentaryRepository;
import com.taskmanager.TaskManager.subtask.dto.SubtaskResponseDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository tRepository;
    private final UserRepository uRepository;
    private final UserService uService;

    public TaskResponseDTO toResponse(Task task) {

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setBody(task.getBody());
        dto.setName(task.getName());
        dto.setOwnerUsername(task.getOwner().getUsername());
        dto.setResponsibleUsername(task.getResponsible().getUsername());
        dto.setInitDate(task.getInitDate());
        dto.setFinishDate(task.getFinishDate());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());

        dto.setCommentary(
                task.getCommentary().stream()
                        .map(commentary -> {
                            CommentResponseDTO cdto = new CommentResponseDTO();
                            cdto.setCommentId(commentary.getId());
                            cdto.setTaskId(commentary.getTask().getId());
                            cdto.setCommentary(commentary.getCommentary());
                            return cdto;
                        })
                        .collect(Collectors.toList())
        );

        dto.setSubtask(
                task.getSubtask().stream()
                        .map(stasks -> {
                            SubtaskResponseDTO sdto = new SubtaskResponseDTO();
                            sdto.setTaskId(stasks.getTask().getId());
                            sdto.setName(stasks.getName());
                            sdto.setBody(stasks.getBody());
                            sdto.setCompleted(stasks.getCompleted());
                            sdto.setOwnerUsername(stasks.getOwner().getUsername());
                            sdto.setResponsibleUsername(stasks.getResponsible().getUsername());
                            sdto.setSubtaskId(stasks.getId());
                            return sdto;
                        })
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public TaskResponseDTO getTaskById(Long taskId) {

        Long companyId = uService.getCurrentCompanyId();

        Task task = tRepository.findByIdAndOwner_Company_Id(taskId, companyId)
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

        Long companyId = uService.getCurrentCompanyId();

        User responsible = uRepository.findByUsernameAndCompany_Id(dto.getResponsibleUsername(), companyId)
                        .orElseThrow(() -> new RuntimeException("User " + dto.getResponsibleUsername() + " not found"));
        task.setResponsible(responsible);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = uRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User " + email + " not found"));

        task.setOwner(owner);

        tRepository.save(task);
        uService.addTaskToList(task, responsible);

        return toResponse(task);

    }

    public List<TaskResponseDTO> userTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = uRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User" + username + "not found"));

        List<Task> tasks = user.getTasks();

        return tasks.stream().map(this::toResponse).toList();

    }

    public List<TaskResponseDTO> tasksByUser(String username) {

        Long companyId = uService.getCurrentCompanyId();

        User user = uRepository.findByUsernameAndCompany_Id(username, companyId)
                .orElseThrow(() -> new EntityNotFoundException("User" + username + "not found"));

        List<Task> tasks = user.getTasks();

        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskResponseDTO editTask(Long taskId, TaskRequestDTO dto) {

        Long companyId = uService.getCurrentCompanyId();

        Task task = tRepository.findByIdAndOwner_Company_Id(taskId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Task" + taskId + "not found"));

        User previousResponsible = task.getResponsible();
        uService.deleteTaskFromList(task, previousResponsible);

        task.setBody(dto.getBody());
        task.setName(dto.getName());
        task.setInitDate(dto.getInitDate());
        task.setFinishDate(dto.getFinishDate());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        User responsible = uRepository.findByUsernameAndCompany_Id(dto.getResponsibleUsername(), companyId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setResponsible(responsible);
        uService.addTaskToList(task, responsible);

        return toResponse(task);

    }

    public void removeTask(Long taskId) {

        Long companyId = uService.getCurrentCompanyId();

        Task task = tRepository.findByIdAndOwner_Company_Id(taskId, companyId)
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
