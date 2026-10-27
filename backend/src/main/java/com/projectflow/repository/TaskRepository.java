package com.projectflow.repository;

import com.projectflow.domain.Task;
import com.projectflow.domain.TaskPriority;
import com.projectflow.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t " +
           "JOIN FETCH t.project p " +
           "JOIN FETCH t.reporter r " +
           "LEFT JOIN FETCH t.assignee a " +
           "WHERE t.id = :id")
    Optional<Task> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT t FROM Task t " +
           "JOIN FETCH t.project p " +
           "JOIN FETCH t.reporter r " +
           "LEFT JOIN FETCH t.assignee a " +
           "WHERE p.id = :projectId " +
           "ORDER BY t.createdAt DESC")
    List<Task> findAllByProjectIdWithDetails(@Param("projectId") Long projectId);

    @Query("SELECT t FROM Task t " +
           "JOIN FETCH t.project p " +
           "JOIN FETCH t.reporter r " +
           "LEFT JOIN FETCH t.assignee a " +
           "WHERE p.id = :projectId AND t.status = :status " +
           "ORDER BY t.createdAt DESC")
    List<Task> findAllByProjectIdAndStatusWithDetails(@Param("projectId") Long projectId, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t " +
           "JOIN FETCH t.project p " +
           "JOIN FETCH t.reporter r " +
           "LEFT JOIN FETCH t.assignee a " +
           "WHERE t.assignee.id = :assigneeId " +
           "ORDER BY t.dueDate ASC NULLS LAST, t.createdAt DESC")
    List<Task> findAllByAssigneeIdWithDetails(@Param("assigneeId") Long assigneeId);

    long countByProjectId(Long projectId);

    long countByStatus(TaskStatus status);

    long countByAssigneeId(Long assigneeId);

    long countByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :assigneeId AND t.dueDate < :today AND t.status <> :doneStatus")
    long countOverdueByAssigneeId(@Param("assigneeId") Long assigneeId,
                                  @Param("today") LocalDate today,
                                  @Param("doneStatus") TaskStatus doneStatus);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.dueDate < :today AND t.status <> :doneStatus")
    long countOverdueByProjectId(@Param("projectId") Long projectId,
                                 @Param("today") LocalDate today,
                                 @Param("doneStatus") TaskStatus doneStatus);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :today AND t.status <> :doneStatus")
    long countOverdueTotal(@Param("today") LocalDate today,
                           @Param("doneStatus") TaskStatus doneStatus);

    @Query("SELECT t FROM Task t " +
           "JOIN FETCH t.project p " +
           "LEFT JOIN FETCH t.assignee a " +
           "WHERE t.dueDate BETWEEN :startDate AND :endDate AND t.status <> :doneStatus " +
           "ORDER BY t.dueDate ASC")
    List<Task> findUpcomingDeadlines(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("doneStatus") TaskStatus doneStatus);

    @Query("SELECT t FROM Task t " +
           "JOIN FETCH t.project p " +
           "LEFT JOIN FETCH t.assignee a " +
           "WHERE t.assignee.id = :assigneeId AND t.dueDate BETWEEN :startDate AND :endDate AND t.status <> :doneStatus " +
           "ORDER BY t.dueDate ASC")
    List<Task> findUpcomingDeadlinesByAssigneeId(@Param("assigneeId") Long assigneeId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("doneStatus") TaskStatus doneStatus);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Task t SET t.assignee = null WHERE t.project.id = :projectId AND t.assignee.id = :userId")
    int unassignUserFromProject(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
