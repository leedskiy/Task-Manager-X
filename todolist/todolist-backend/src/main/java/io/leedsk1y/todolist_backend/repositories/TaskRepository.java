package io.leedsk1y.todolist_backend.repositories;

import io.leedsk1y.todolist_backend.models.ETaskStatus;
import io.leedsk1y.todolist_backend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByUserId(UUID userId);

    List<Task> findByUserIdAndStatus(UUID userId, ETaskStatus status);
}
