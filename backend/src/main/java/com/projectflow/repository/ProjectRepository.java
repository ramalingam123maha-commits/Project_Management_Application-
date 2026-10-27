package com.projectflow.repository;

import com.projectflow.domain.Project;
import com.projectflow.domain.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p JOIN FETCH p.owner WHERE p.id = :id")
    Optional<Project> findByIdWithOwner(@Param("id") Long id);

    @Query("SELECT p FROM Project p JOIN FETCH p.owner ORDER BY p.createdAt DESC")
    List<Project> findAllWithOwner();

    @Query("SELECT p FROM Project p JOIN FETCH p.owner WHERE p.owner.id = :ownerId ORDER BY p.createdAt DESC")
    List<Project> findAllByOwnerIdWithOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT DISTINCT p FROM Project p JOIN FETCH p.owner JOIN ProjectMember pm ON pm.project = p WHERE pm.user.id = :userId ORDER BY p.createdAt DESC")
    List<Project> findAllByMemberUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM Project p JOIN FETCH p.owner LEFT JOIN ProjectMember pm ON pm.project = p WHERE p.owner.id = :userId OR pm.user.id = :userId ORDER BY p.createdAt DESC")
    List<Project> findAllAccessibleByUser(@Param("userId") Long userId);

    long countByStatus(ProjectStatus status);

    long countByOwnerId(Long ownerId);

    long countByOwnerIdAndStatus(Long ownerId, ProjectStatus status);

    @Query("SELECT COUNT(DISTINCT p) FROM Project p LEFT JOIN ProjectMember pm ON pm.project = p WHERE (p.owner.id = :userId OR pm.user.id = :userId) AND p.status = :status")
    long countAccessibleByUserAndStatus(@Param("userId") Long userId, @Param("status") ProjectStatus status);

    @Query("SELECT COUNT(DISTINCT p) FROM Project p LEFT JOIN ProjectMember pm ON pm.project = p WHERE p.owner.id = :userId OR pm.user.id = :userId")
    long countAccessibleByUser(@Param("userId") Long userId);
}
