package com.taskmanager.TaskManager.task.repository;

import com.taskmanager.TaskManager.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>{
}
