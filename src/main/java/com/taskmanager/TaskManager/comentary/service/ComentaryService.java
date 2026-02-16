package com.taskmanager.TaskManager.comentary.service;

import com.taskmanager.TaskManager.comentary.dto.ComentRequestDTO;
import com.taskmanager.TaskManager.comentary.dto.ComentResponseDTO;
import com.taskmanager.TaskManager.comentary.entity.Comentary;
import com.taskmanager.TaskManager.comentary.repository.ComentaryRepository;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentaryService {

    private final ComentaryRepository comentRepository;
    private final TaskRepository tRepository;

    public ComentaryService(ComentaryRepository comentRepository, TaskRepository tRepository) {
        this.comentRepository = comentRepository;
        this.tRepository = tRepository;
    }

    public ComentResponseDTO toResponse(Comentary comentary) {

        ComentResponseDTO newComent = new ComentResponseDTO();
        newComent.setComentary(comentary.getComentary());
        newComent.setTask(comentary.getTask());

        return newComent;

    }

    public List<ComentResponseDTO> getTaskComents(Long taskId) {

        Task task = tRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return task.getComentary().stream().map(this::toResponse).toList();

    }
}
