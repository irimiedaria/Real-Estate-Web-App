package com.projectps.buildingmanagement.repositories;

import com.projectps.buildingmanagement.entities.Solicitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitationRepository extends JpaRepository<Solicitation, UUID>  {

    List<Solicitation> findByUserId(UUID userId);
    Optional<Solicitation> findByIdAndUserId(UUID id, UUID userId);

    List<Solicitation> findByUserIdAndPropertyId(UUID userId, UUID propertyId);
}
