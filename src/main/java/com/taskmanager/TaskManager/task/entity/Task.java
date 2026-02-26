package com.taskmanager.TaskManager.task.entity;

import com.taskmanager.TaskManager.commentary.entity.Commentary;
import com.taskmanager.TaskManager.subtask.entity.Subtask;
import com.taskmanager.TaskManager.task.Priority;
import com.taskmanager.TaskManager.task.TaskStatus;
import com.taskmanager.TaskManager.users.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String body;
    @NotNull
    private TaskStatus status;
    @NotNull
    private Priority priority;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Commentary> commentary = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @ManyToOne(optional = false)
    @JoinColumn(name = "responsible_id", nullable = false)
    private User responsible;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Subtask> subtask = new ArrayList<>();

    @NotNull
    private LocalDateTime initDate;
    @NotNull
    private LocalDateTime finishDate;

}
