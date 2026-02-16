package com.taskmanager.TaskManager.comentary.entity;

import com.taskmanager.TaskManager.task.entity.Task;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comentary")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Comentary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comentary;

    @OneToOne(optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

}
