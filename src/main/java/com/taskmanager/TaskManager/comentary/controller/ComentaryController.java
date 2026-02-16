package com.taskmanager.TaskManager.comentary.controller;

import com.taskmanager.TaskManager.comentary.dto.ComentResponseDTO;
import com.taskmanager.TaskManager.comentary.service.ComentaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comentary")
public class ComentaryController {

    private final ComentaryService comentService;

    public ComentaryController(ComentaryService comentaryService) {
        this.comentService = comentaryService;
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<ComentResponseDTO>> getAllComentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(comentService.getTaskComents(taskId));
    }


}
