package com.taskmanager.TaskManager.commentary.dto;

import com.taskmanager.TaskManager.task.entity.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {

    private String commentary;
    private Task task;

}
