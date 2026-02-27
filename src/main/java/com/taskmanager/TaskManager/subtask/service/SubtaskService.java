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
import com.taskmanager.TaskManager.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SubtaskService {

    private final SubtaskRepository sRepository;
    private final UserRepository uRepository;
    private final TaskRepository tRepository;
    private final TaskService tService;
    private final UserService uService;

    public SubtaskService(SubtaskRepository sRepository,
                          UserRepository uRepository,
                          TaskRepository tRepository,
                          TaskService tService,
                          UserService uService)
    {
        this.sRepository = sRepository;
        this.uRepository = uRepository;
        this.tRepository = tRepository;
        this.tService = tService;
        this.uService = uService;
    }

    public SubtaskResponseDTO toResponse(Subtask stask) {

        SubtaskResponseDTO response = new SubtaskResponseDTO();
        response.setSubtaskId(stask.getId());
        response.setBody(stask.getBody());
        response.setResponsibleUsername(stask.getResponsible().getUsername());
        response.setOwnerUsername(stask.getOwner().getUsername());
        response.setName(stask.getName());
        response.setTaskId(stask.getTask().getId());
        response.setCompleted(stask.getCompleted());

        return response;
    }

    public SubtaskResponseDTO getSubtask(Long subtaskId) {
        Long companyId = uService.getCurrentCompanyId();
        Subtask stask = sRepository.findByIdAndCompanyId(subtaskId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        return toResponse(stask);
    }

    public SubtaskResponseDTO createSubtask(SubtaskRequestDTO dto) {

        Long companyId = uService.getCurrentCompanyId();

        Subtask stask = new Subtask();
        stask.setBody(dto.getBody());
        stask.setName(dto.getName());
        stask.setCompleted(false);

        User responsible = uRepository.findByUsername(dto.getResponsibleUsername(), companyId)
                .orElseThrow(() -> new EntityNotFoundException("Username " + dto.getResponsibleUsername() + " not found"));
        stask.setResponsible(responsible);

        String ownerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = uRepository.findByUsername(ownerUsername, companyId)
                .orElseThrow(() -> new EntityNotFoundException("User " + ownerUsername + " not found"));
        stask.setOwner(owner);

        Long taskId = dto.getTaskId();
        Task task = tRepository.findByIdAndCompanyId(taskId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Task " + taskId + " not found"));
        stask.setTask(task);

        tService.addSubtaskToTask(stask.getTask(), stask);
        sRepository.save(stask);

        return toResponse(stask);

    }

    public void markAsCompleted(Long subtaskId) {

        Long companyId = uService.getCurrentCompanyId();

        Subtask stask = sRepository.findByIdAndCompanyId(subtaskId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        stask.setCompleted(true);
    }

    public SubtaskResponseDTO editSubtask(Long subtaskId, SubtaskRequestDTO dto) {

        Long companyId = uService.getCurrentCompanyId();

        Subtask stask = sRepository.findByIdAndCompanyId(subtaskId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        stask.setName(dto.getName());
        stask.setBody(dto.getBody());

        User responsible = uRepository.findByUsername(dto.getResponsibleUsername(), companyId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
        stask.setResponsible(responsible);

        Task previousTask = tRepository.findByIdAndCompanyId(stask.getTask().getId(), companyId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        Task task = tRepository.findByIdAndCompanyId(dto.getTaskId(), companyId)
                        .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        stask.setTask(task);

        tService.removeSubtaskFromTask(previousTask, stask);
        tService.addSubtaskToTask(task, stask);

        return toResponse(stask);

    }

    public void removeSubtask(Long subtaskId) {

        Long companyId = uService.getCurrentCompanyId();

        Subtask stask = sRepository.findByIdAndCompanyId(subtaskId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        sRepository.delete(stask);
    }

    public List<SubtaskResponseDTO> getSubtasksByTask(Long taskId) {

        Long companyId = uService.getCurrentCompanyId();

        Task task = tRepository.findByIdAndCompanyId(taskId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        List<Subtask> subtasks = task.getSubtask();

        return subtasks.stream().map(this::toResponse).toList();
    }

}
