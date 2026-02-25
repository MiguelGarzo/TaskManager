package com.taskmanager.TaskManager.commentary.repository;

import com.taskmanager.TaskManager.commentary.entity.Commentary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentaryRepository extends JpaRepository<Commentary, Long> {
    Optional<Commentary> findById(Long ComentId);
}
