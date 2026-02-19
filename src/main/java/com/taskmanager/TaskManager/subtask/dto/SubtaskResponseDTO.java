package com.taskmanager.TaskManager.subtask.dto;

import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.users.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubtaskResponseDTO {

    private String name;
    private String body;
    private Task task;
    private User owner;
    private User responsible;
    private Boolean completed;

}
