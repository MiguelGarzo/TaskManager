package com.taskmanager.TaskManager.company.entity;

import com.taskmanager.TaskManager.users.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    private String adress;

}
