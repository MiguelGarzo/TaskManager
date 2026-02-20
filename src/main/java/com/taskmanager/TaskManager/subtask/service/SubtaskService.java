package com.taskmanager.TaskManager.subtask.service;

import com.taskmanager.TaskManager.subtask.dto.SubtaskRequestDTO;
import com.taskmanager.TaskManager.subtask.dto.SubtaskResponseDTO;
import com.taskmanager.TaskManager.subtask.entity.Subtask;
import com.taskmanager.TaskManager.subtask.repository.SubtaskRepository;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.task.repository.TaskRepository;
import com.taskmanager.TaskManager.task.service.TaskService;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SubtaskService {

    private SubtaskRepository sRepository;
    private UserRepository uRepository;
    private TaskRepository tRepository;
    private TaskService tService;

    public SubtaskService(SubtaskRepository sRepository,
                          UserRepository uRepository,
                          TaskRepository tRepository,
                          TaskService tService)
    {
        this.sRepository = sRepository;
        this.uRepository = uRepository;
        this.tRepository = tRepository;
        this.tService = tService;
    }

    public SubtaskResponseDTO toResponse(Subtask stask) {

        SubtaskResponseDTO response = new SubtaskResponseDTO();
        response.setSubtaskId(stask.getId());
        response.setBody(stask.getBody());
        response.setResponsible(stask.getResponsible());
        response.setOwner(stask.getOwner());
        response.setName(stask.getName());
        response.setTask(stask.getTask());
        response.setCompleted(stask.getCompleted());

        return response;
    }

    public SubtaskResponseDTO getSubtask(Long subtaskId) {
        Subtask stask = sRepository.findById(subtaskId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        return toResponse(stask);
    }

    public SubtaskResponseDTO createSubtask(SubtaskRequestDTO dto) {

        Subtask stask = new Subtask();
        stask.setBody(dto.getBody());
        stask.setName(dto.getName());
        stask.setCompleted(false);

        User responsible = uRepository.findByUsername(dto.getResponsibleUsername())
                .orElseThrow(() -> new EntityNotFoundException("Username " + dto.getResponsibleUsername() + " not found"));
        stask.setResponsible(responsible);

        String ownerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = uRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new EntityNotFoundException("User " + ownerUsername + " not found"));
        stask.setOwner(owner);

        Long taskId = dto.getTaskId();
        Task task = tRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task " + taskId + " not found"));
        stask.setTask(task);

        tService.addSubtaskToTask(stask.getTask().getId(), stask.getId());
        sRepository.save(stask);

        return toResponse(stask);

    }

    public void markAsCompleted(Long subtaskId) {
        Subtask stask = sRepository.findById(subtaskId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        stask.setCompleted(true);
    }

    public SubtaskResponseDTO editSubtask(SubtaskRequestDTO dto) {

        Subtask stask = sRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        stask.setName(dto.getName());
        stask.setBody(dto.getBody());

        User responsible = uRepository.findByUsername(dto.getResponsibleUsername())
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
        stask.setResponsible(responsible);

        Task previousTask = tRepository.findById(stask.getTask().getId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        tService.removeSubtaskFromTask(previousTask.getId(), stask.getId());

        Task task = tRepository.findById(dto.getTaskId())
                        .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        stask.setTask(task);
        tService.addSubtaskToTask(task.getId(), stask.getId());

        return toResponse(stask);

    }

    public void removeSubtask(Long subtaskId) {
        Subtask stask = sRepository.findById(subtaskId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        sRepository.delete(stask);
    }

    public List<SubtaskResponseDTO> getSubtasksByTask(Long taskId) {

        Task task = tRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        List<Subtask> subtasks = task.getSubtask();

        return subtasks.stream().map(this::toResponse).toList();
    }

}
