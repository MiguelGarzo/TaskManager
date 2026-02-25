package com.taskmanager.TaskManager.task.dto;

import com.taskmanager.TaskManager.task.Priority;
import com.taskmanager.TaskManager.task.TaskStatus;
import com.taskmanager.TaskManager.users.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskRequestDTO {

    private String name;
    private String body;
    private String responsibleUsername;
    private Date initDate;
    private Date finishDate;
    private TaskStatus status;
    private Priority priority;

}
