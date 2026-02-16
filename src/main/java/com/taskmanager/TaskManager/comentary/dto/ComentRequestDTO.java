package com.taskmanager.TaskManager.comentary.dto;

import com.taskmanager.TaskManager.task.entity.Task;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComentRequestDTO {

    private Long id;
    private String comentary;
    private Task task;

}
