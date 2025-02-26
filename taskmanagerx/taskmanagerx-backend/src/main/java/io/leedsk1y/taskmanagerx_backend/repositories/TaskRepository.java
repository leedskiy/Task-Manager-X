package io.leedsk1y.taskmanagerx_backend.repositories;

import io.leedsk1y.taskmanagerx_backend.models.ETaskStatus;
import io.leedsk1y.taskmanagerx_backend.models.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findTasksByUserId (UUID userId);

    List<Task> findTasksByUserId (UUID userId, Sort sort);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:dueDateBefore IS NULL OR t.dueDate < :dueDateBefore) " +
            "AND (:dueDateAfter IS NULL OR t.dueDate > :dueDateAfter)")
    List<Task> findTasksByFilters(
            @Param("userId") UUID userId,
            @Param("status") ETaskStatus status,
            @Param("dueDateBefore") LocalDateTime dueDateBefore,
            @Param("dueDateAfter") LocalDateTime dueDateAfter);

    @Query("SELECT t FROM Task t WHERE (:status IS NULL OR t.status = :status) " +
            "AND (:dueDateBefore IS NULL OR t.dueDate < :dueDateBefore) " +
            "AND (:dueDateAfter IS NULL OR t.dueDate > :dueDateAfter) " +
            "AND (:userId IS NULL OR t.user.id = :userId)")
    List<Task> findTasksByFiltersForAdminAndUser(@Param("userId") UUID userId,
                                                 @Param("status") ETaskStatus status,
                                                 @Param("dueDateBefore") LocalDateTime dueDateBefore,
                                                 @Param("dueDateAfter") LocalDateTime dueDateAfter);
}
