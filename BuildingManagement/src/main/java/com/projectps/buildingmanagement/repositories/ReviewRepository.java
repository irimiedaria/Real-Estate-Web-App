package com.projectps.buildingmanagement.repositories;

import com.projectps.buildingmanagement.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>  {
    List<Review> findByUserId(UUID userId);
    Optional<Review> findByIdAndUserId(UUID id, UUID userId);
}
