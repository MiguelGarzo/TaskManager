package com.taskmanager.TaskManager.subtask.repository;

import com.taskmanager.TaskManager.subtask.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
    Optional<Subtask> findByIdAndOwner_Company_Id(Long subtaskId, Long companyId);
}
