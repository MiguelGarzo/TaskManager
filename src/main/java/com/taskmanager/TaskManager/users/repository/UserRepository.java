package com.taskmanager.TaskManager.users.repository;

import com.taskmanager.TaskManager.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long > {
    Optional<User> findByUsername(String username);
    Optional<User> findByCompanyId(Long companyId);
}
