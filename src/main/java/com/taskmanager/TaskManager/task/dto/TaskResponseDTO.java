package com.taskmanager.TaskManager.task.dto;

import com.taskmanager.TaskManager.comentary.entity.Comentary;
import com.taskmanager.TaskManager.comentary.subtask.Subtask;
import com.taskmanager.TaskManager.users.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TaskResponseDTO {

    private Long id;
    private String name;
    private String body;
    private List<Comentary> comentary = new ArrayList<>();
    private User owner;
    private User responsible;
    private List<Subtask> subtask = new ArrayList<>();
    private Date initDate;
    private Date finishDate;

}
