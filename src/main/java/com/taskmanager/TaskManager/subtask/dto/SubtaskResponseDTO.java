package com.taskmanager.TaskManager.subtask.dto;

import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.users.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubtaskResponseDTO {

    private Long subtaskId;
    private String name;
    private String body;
    private Long taskId;
    private String ownerUsername;
    private String responsibleUsername;
    private Boolean completed;

}
