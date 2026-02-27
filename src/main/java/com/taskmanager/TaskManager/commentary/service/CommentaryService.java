package com.taskmanager.TaskManager.commentary.service;

import com.taskmanager.TaskManager.commentary.dto.CommentRequestDTO;
import com.taskmanager.TaskManager.commentary.dto.CommentResponseDTO;
import com.taskmanager.TaskManager.commentary.entity.Commentary;
import com.taskmanager.TaskManager.commentary.repository.CommentaryRepository;
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
public class CommentaryService {

    private final CommentaryRepository commentRepository;
    private final TaskRepository tRepository;
    private final TaskService tService;
    private final UserRepository uRepository;
    private final UserService uService;

    public CommentaryService(CommentaryRepository commentRepository,
                             TaskRepository tRepository,
                             TaskService tService,
                             UserRepository uRepository,
                             UserService uService)
    {
        this.commentRepository = commentRepository;
        this.tRepository = tRepository;
        this.tService = tService;
        this.uRepository = uRepository;
        this.uService = uService;
    }

    public CommentResponseDTO toResponse(Commentary commentary) {

        CommentResponseDTO newComment = new CommentResponseDTO();
        newComment.setCommentary(commentary.getCommentary());
        newComment.setTaskId(commentary.getTask().getId());
        newComment.setCommentId(commentary.getId());

        return newComment;

    }

    public CommentResponseDTO createComment(CommentRequestDTO dto) {

        Long companyId = uService.getCurrentCompanyId();

        Commentary comment = new Commentary();
        comment.setCommentary(dto.getCommentary());

        Task task = tRepository.findByIdAndCompanyId(dto.getTaskId(), companyId)
                        .orElseThrow(() -> new EntityNotFoundException("Task " + dto.getTaskId() + " not found"));

        comment.setTask(task);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = uRepository.findByUsername(username, companyId)
                        .orElseThrow(() -> new EntityNotFoundException("User " + username + " not found"));
        comment.setOwner(owner);

        tService.addComentToTask(comment.getTask(), comment);
        comment.getOwner().getComments().add(comment);
        commentRepository.save(comment);

        return toResponse(comment);
    }

    public List<CommentResponseDTO> getTaskComments(Long taskId) {
        Long companyId = uService.getCurrentCompanyId();
        Task task = tRepository.findByIdAndCompanyId(taskId, companyId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return task.getCommentary().stream().map(this::toResponse).toList();

    }

    public CommentResponseDTO editBody(Long commentId, CommentRequestDTO dto) {
        Long companyId = uService.getCurrentCompanyId();
        Commentary comment = commentRepository.findByIdAndCompanyId(commentId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Coment not found"));

        comment.setCommentary(dto.getCommentary());

        return toResponse(comment);
    }

    public void removeComment(Long commentId) {
        Long companyId = uService.getCurrentCompanyId();
        Commentary comment = commentRepository.findByIdAndCompanyId(commentId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Coment not found"));

        comment.getOwner().getComments().remove(comment);

        commentRepository.delete(comment);
    }
}
