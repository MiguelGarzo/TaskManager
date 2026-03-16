package com.taskmanager.TaskManager.task.dto;

import com.taskmanager.TaskManager.task.Priority;
import com.taskmanager.TaskManager.task.TaskStatus;
import com.taskmanager.TaskManager.users.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class TaskRequestDTO {

    private String name;
    private String body;
    private String responsibleUsername;
    private LocalDateTime initDate;
    private LocalDateTime finishDate;
    private TaskStatus status;
    private Priority priority;

}
