package com.taskmanager.TaskManager.comentary.repository;

import com.taskmanager.TaskManager.comentary.dto.ComentResponseDTO;
import com.taskmanager.TaskManager.comentary.entity.Comentary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComentaryRepository extends JpaRepository<Comentary, Long> {
    Optional<Comentary> findById(Long ComentId);
}
