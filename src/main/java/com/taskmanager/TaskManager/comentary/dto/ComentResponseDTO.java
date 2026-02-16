package com.taskmanager.TaskManager.comentary.dto;

import com.taskmanager.TaskManager.task.entity.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComentResponseDTO {

    private String comentary;
    private Task task;

}
