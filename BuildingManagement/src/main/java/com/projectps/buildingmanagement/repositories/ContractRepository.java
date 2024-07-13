package com.projectps.buildingmanagement.repositories;

import com.projectps.buildingmanagement.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {
    List<Contract> findByUserId(UUID userId);
    Optional<Contract> findByIdAndUserId(UUID id, UUID userId);
}
