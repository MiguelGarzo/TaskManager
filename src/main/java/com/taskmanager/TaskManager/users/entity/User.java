package com.taskmanager.TaskManager.users.entity;

import com.taskmanager.TaskManager.commentary.entity.Commentary;
import com.taskmanager.TaskManager.company.entity.Company;
import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.users.Role;
import com.taskmanager.TaskManager.users.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "company_id"})
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email can't be empty")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Username can't be empty")
    @Column(nullable = false)
    private String username;

    @NotBlank(message = "Password can't be empty")
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NORMAL;

    @OneToMany(mappedBy = "responsible", cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Commentary> comments = new ArrayList<>();

    private LocalDateTime premiumExpiration;


}