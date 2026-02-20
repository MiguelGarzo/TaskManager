package com.taskmanager.TaskManager.comentary.dto;

import com.taskmanager.TaskManager.task.entity.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDTO {

    private Long commentId;
    private String commentary;
    private Task task;

}
