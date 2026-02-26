package com.taskmanager.TaskManager.commentary.controller;

import com.taskmanager.TaskManager.commentary.dto.CommentRequestDTO;
import com.taskmanager.TaskManager.commentary.dto.CommentResponseDTO;
import com.taskmanager.TaskManager.commentary.service.CommentaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commentary")
public class CommentaryController {

    private final CommentaryService commentService;

    public CommentaryController(CommentaryService commentaryService) {
        this.commentService = commentaryService;
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponseDTO>> getAllCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getTaskComments(taskId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> editBodyComment(@PathVariable Long commentId, @RequestBody CommentRequestDTO dto) {
        return ResponseEntity.ok(commentService.editBody(commentId, dto));
    }

    @PostMapping()
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO dto) {
        return ResponseEntity.ok(commentService.createComment(dto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.removeComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
