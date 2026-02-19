package com.taskmanager.TaskManager.subtask.service;

import com.taskmanager.TaskManager.subtask.dto.SubtaskRequestDTO;
import com.taskmanager.TaskManager.subtask.dto.SubtaskResponseDTO;
import com.taskmanager.TaskManager.subtask.entity.Subtask;
import com.taskmanager.TaskManager.subtask.repository.SubtaskRepository;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.task.repository.TaskRepository;
import com.taskmanager.TaskManager.users.entity.User;
import com.taskmanager.TaskManager.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SubtaskService {

    private SubtaskRepository sRepository;
    private UserRepository uRepository;
    private TaskRepository tRepository;

    public SubtaskService(SubtaskRepository sRepository, UserRepository uRepository, TaskRepository tRepository) {
        this.sRepository = sRepository;
        this.uRepository = uRepository;
        this.tRepository = tRepository;
    }

    public SubtaskResponseDTO toResponse(Subtask stask) {

        SubtaskResponseDTO response = new SubtaskResponseDTO();
        response.setBody(stask.getBody());
        response.setResponsible(stask.getResponsible());
        response.setOwner(stask.getOwner());
        response.setName(stask.getName());
        response.setTask(stask.getTask());
        response.setCompleted(stask.getCompleted());

        return response;
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

        Task task = tRepository.findById(dto.getTaskId())
                        .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        stask.setTask(task);

        return toResponse(stask);

    }

    public void removeSubtask(Long subtaskId) {
        Subtask stask = sRepository.findById(subtaskId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        sRepository.delete(stask);
    }

}
