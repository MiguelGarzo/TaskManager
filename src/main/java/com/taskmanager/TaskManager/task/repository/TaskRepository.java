package com.taskmanager.TaskManager.task.repository;

import com.taskmanager.TaskManager.task.entity.Task;
import com.taskmanager.TaskManager.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>{
    Optional<Task> findByIdAndOwner_Company_Id(Long taskId, Long companyId);
}
