package com.taskmanager.TaskManager.commentary.service;

import com.taskmanager.TaskManager.commentary.dto.CommentRequestDTO;
import com.taskmanager.TaskManager.commentary.dto.CommentResponseDTO;
import com.taskmanager.TaskManager.commentary.entity.Commentary;
import com.taskmanager.TaskManager.commentary.repository.CommentaryRepository;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.task.repository.TaskRepository;
import com.taskmanager.TaskManager.task.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CommentaryService {

    private final CommentaryRepository commentRepository;
    private final TaskRepository tRepository;
    private final TaskService tService;

    public CommentaryService(CommentaryRepository commentRepository,
                             TaskRepository tRepository,
                             TaskService tService)
    {
        this.commentRepository = commentRepository;
        this.tRepository = tRepository;
        this.tService = tService;
    }

    public CommentResponseDTO toResponse(Commentary commentary) {

        CommentResponseDTO newComent = new CommentResponseDTO();
        newComent.setCommentary(commentary.getCommentary());
        newComent.setTask(commentary.getTask());

        return newComent;

    }

    public CommentResponseDTO createComment(CommentRequestDTO dto) {

        Commentary comment = new Commentary();
        comment.setCommentary(dto.getCommentary());
        comment.setTask(dto.getTask());

        tService.addComentToTask(comment.getTask(), comment);
        comment.getOwner().getComents().add(comment);
        commentRepository.save(comment);

        return toResponse(comment);
    }

    public List<CommentResponseDTO> getTaskComments(Long taskId) {

        Task task = tRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return task.getCommentary().stream().map(this::toResponse).toList();

    }

    public CommentResponseDTO editBody(Long commentId, CommentRequestDTO dto) {
        Commentary comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Coment not found"));

        comment.setCommentary(dto.getCommentary());

        return toResponse(comment);
    }

    public void removeComment(Long commentId) {
        Commentary comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Coment not found"));

        comment.getOwner().getComents().remove(comment);

        commentRepository.delete(comment);
    }
}
