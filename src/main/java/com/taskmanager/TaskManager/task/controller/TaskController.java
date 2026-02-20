package com.taskmanager.TaskManager.task.controller;

import com.taskmanager.TaskManager.task.dto.TaskRequestDTO;
import com.taskmanager.TaskManager.task.dto.TaskResponseDTO;
import com.taskmanager.TaskManager.task.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService tService;

    public TaskController(TaskService tService) {
        this.tService = tService;
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity.ok(tService.getTaskById(taskId));
    }

    @PostMapping()
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tService.createTask(dto));
    }

    @GetMapping()
    public ResponseEntity<List<TaskResponseDTO>> userTasks() {
        return ResponseEntity.ok(tService.userTasks());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<List<TaskResponseDTO>> tasksByUser(@PathVariable String username) {
        return ResponseEntity.ok(tService.tasksByUser(username));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> editTask(@PathVariable Long taskId, @RequestBody TaskRequestDTO dto) {
        return ResponseEntity.ok(tService.editTask(taskId, dto));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeTask(@PathVariable Long taskId) {
        tService.removeTask(taskId);
        return ResponseEntity.noContent().build();
    }

}
