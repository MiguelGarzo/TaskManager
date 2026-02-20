package com.taskmanager.TaskManager.comentary.repository;

import com.taskmanager.TaskManager.comentary.entity.Commentary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentaryRepository extends JpaRepository<Commentary, Long> {
    Optional<Commentary> findById(Long ComentId);
}
