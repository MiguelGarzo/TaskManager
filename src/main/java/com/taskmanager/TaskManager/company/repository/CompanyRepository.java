package com.taskmanager.TaskManager.company.repository;

import com.taskmanager.TaskManager.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Override
    Optional<Company> findById(Long companyId);

    Optional<Company> findByName(String name);

    boolean existsByName(String name);
}
