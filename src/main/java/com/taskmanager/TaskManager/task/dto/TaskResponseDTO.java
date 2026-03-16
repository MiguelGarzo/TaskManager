package com.taskmanager.TaskManager.task.dto;

import com.taskmanager.TaskManager.commentary.dto.CommentResponseDTO;
import com.taskmanager.TaskManager.commentary.entity.Commentary;
import com.taskmanager.TaskManager.subtask.dto.SubtaskResponseDTO;
import com.taskmanager.TaskManager.subtask.entity.Subtask;
import com.taskmanager.TaskManager.task.Priority;
import com.taskmanager.TaskManager.task.TaskStatus;
import com.taskmanager.TaskManager.users.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TaskResponseDTO {

    private Long id;
    private String name;
    private String body;
    private List<CommentResponseDTO> commentary = new ArrayList<>();
    private String ownerUsername;
    private String responsibleUsername;
    private List<SubtaskResponseDTO> subtask = new ArrayList<>();
    private LocalDateTime initDate;
    private LocalDateTime finishDate;
    private TaskStatus status;
    private Priority priority;

}
