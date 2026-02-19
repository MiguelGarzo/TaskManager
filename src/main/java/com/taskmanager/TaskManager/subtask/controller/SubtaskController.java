package com.taskmanager.TaskManager.subtask.controller;

import com.taskmanager.TaskManager.subtask.service.SubtaskService;
import com.taskmanager.TaskManager.subtask.dto.SubtaskRequestDTO;
import com.taskmanager.TaskManager.subtask.dto.SubtaskResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subtask")
public class SubtaskController {

    private final SubtaskService sService;

    public SubtaskController(SubtaskService sService) {
        this.sService = sService;
    }

    @PostMapping
    public ResponseEntity<SubtaskResponseDTO> createSubtask(@RequestBody SubtaskRequestDTO dto) {
        return ResponseEntity.ok(sService.createSubtask(dto));
    }

    @PatchMapping("/complete/{subtaskId}")
    public ResponseEntity<Void> markAsCompleted(@PathVariable Long subtaskId) {
        sService.markAsCompleted(subtaskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping()
    public ResponseEntity<SubtaskResponseDTO> editSubtask(@RequestBody SubtaskRequestDTO dto) {

        return ResponseEntity.ok(sService.editSubtask(dto));

    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteSubtask(@PathVariable Long taskId) {
        sService.removeSubtask(taskId);
        return ResponseEntity.noContent().build();
    }


}
