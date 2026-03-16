package com.taskmanager.TaskManager.subtask.controller;

import com.taskmanager.TaskManager.subtask.service.SubtaskService;
import com.taskmanager.TaskManager.subtask.dto.SubtaskRequestDTO;
import com.taskmanager.TaskManager.subtask.dto.SubtaskResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subtask")
public class SubtaskController {

    private final SubtaskService sService;

    public SubtaskController(SubtaskService sService) {
        this.sService = sService;
    }

    @GetMapping("/{subtaskId}")
    public ResponseEntity<SubtaskResponseDTO> getSubtask(@PathVariable Long subtaskId) {
        return ResponseEntity.ok(sService.getSubtask(subtaskId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<SubtaskResponseDTO>> subtasksByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(sService.getSubtasksByTask(taskId));
    }

    @PostMapping
    public ResponseEntity<SubtaskResponseDTO> createSubtask(@RequestBody SubtaskRequestDTO dto) {
        return ResponseEntity.ok(sService.createSubtask(dto));
    }

    @PatchMapping("/complete/{subtaskId}")
    public ResponseEntity<Void> markAsCompleted(@PathVariable Long subtaskId) {
        sService.markAsCompleted(subtaskId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{subtaskId}")
    public ResponseEntity<SubtaskResponseDTO> editSubtask(@PathVariable Long subtaskId, @RequestBody SubtaskRequestDTO dto) {

        return ResponseEntity.ok(sService.editSubtask(subtaskId, dto));

    }

    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(@PathVariable Long subtaskId) {
        sService.removeSubtask(subtaskId);
        return ResponseEntity.noContent().build();
    }


}
