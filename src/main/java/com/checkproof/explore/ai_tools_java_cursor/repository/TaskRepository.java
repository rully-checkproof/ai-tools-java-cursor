package com.checkproof.explore.ai_tools_java_cursor.repository;

import com.checkproof.explore.ai_tools_java_cursor.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by date range
    @Query("SELECT t FROM Task t WHERE t.startDate BETWEEN :startDate AND :endDate ORDER BY t.startDate ASC")
    List<Task> findTasksByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);

    // Find tasks by date range with pagination
    @Query("SELECT t FROM Task t WHERE t.startDate BETWEEN :startDate AND :endDate ORDER BY t.startDate ASC")
    Page<Task> findTasksByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate, 
                                   Pageable pageable);

    // Find upcoming tasks within next 7 days
    @Query("SELECT t FROM Task t WHERE t.startDate BETWEEN :now AND :sevenDaysLater ORDER BY t.startDate ASC")
    List<Task> findUpcomingTasks(@Param("now") LocalDateTime now, 
                                @Param("sevenDaysLater") LocalDateTime sevenDaysLater);

    // Find upcoming tasks with pagination
    @Query("SELECT t FROM Task t WHERE t.startDate BETWEEN :now AND :sevenDaysLater ORDER BY t.startDate ASC")
    Page<Task> findUpcomingTasks(@Param("now") LocalDateTime now, 
                                @Param("sevenDaysLater") LocalDateTime sevenDaysLater, 
                                Pageable pageable);

    // Find tasks by priority
    List<Task> findByPriorityOrderByStartDateAsc(Task.Priority priority);

    // Find tasks by status
    List<Task> findByStatusOrderByStartDateAsc(Task.TaskStatus status);

    // Find tasks by priority and status
    List<Task> findByPriorityAndStatusOrderByStartDateAsc(Task.Priority priority, Task.TaskStatus status);

    // Find tasks by priority and status with pagination
    Page<Task> findByPriorityAndStatusOrderByStartDateAsc(Task.Priority priority, 
                                                         Task.TaskStatus status, 
                                                         Pageable pageable);

    // Find overdue tasks (tasks with end date in the past and not completed)
    @Query("SELECT t FROM Task t WHERE t.endDate < :now AND t.status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY t.endDate ASC")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    // Find overdue tasks with pagination
    @Query("SELECT t FROM Task t WHERE t.endDate < :now AND t.status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY t.endDate ASC")
    Page<Task> findOverdueTasks(@Param("now") LocalDateTime now, Pageable pageable);

    // Find recurring tasks
    @Query("SELECT t FROM Task t WHERE t.recurrencePattern IS NOT NULL ORDER BY t.startDate ASC")
    List<Task> findRecurringTasks();

    // Find recurring tasks with pagination
    @Query("SELECT t FROM Task t WHERE t.recurrencePattern IS NOT NULL ORDER BY t.startDate ASC")
    Page<Task> findRecurringTasks(Pageable pageable);

    // Find tasks by participant
    @Query("SELECT t FROM Task t JOIN t.participants p WHERE p.id = :participantId ORDER BY t.startDate ASC")
    List<Task> findTasksByParticipantId(@Param("participantId") Long participantId);

    // Find tasks by participant with pagination
    @Query("SELECT t FROM Task t JOIN t.participants p WHERE p.id = :participantId ORDER BY t.startDate ASC")
    Page<Task> findTasksByParticipantId(@Param("participantId") Long participantId, Pageable pageable);

    // Find tasks by title containing (case-insensitive)
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY t.startDate ASC")
    List<Task> findByTitleContainingIgnoreCase(@Param("title") String title);

    // Find tasks by title containing with pagination
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY t.startDate ASC")
    Page<Task> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    // Count tasks by status
    long countByStatus(Task.TaskStatus status);

    // Count tasks by priority
    long countByPriority(Task.Priority priority);

    // Count overdue tasks
    @Query("SELECT COUNT(t) FROM Task t WHERE t.endDate < :now AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    long countOverdueTasks(@Param("now") LocalDateTime now);

    // Count upcoming tasks
    @Query("SELECT COUNT(t) FROM Task t WHERE t.startDate BETWEEN :now AND :sevenDaysLater")
    long countUpcomingTasks(@Param("now") LocalDateTime now, @Param("sevenDaysLater") LocalDateTime sevenDaysLater);
} 