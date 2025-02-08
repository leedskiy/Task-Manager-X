package io.leedsk1y.todolist_backend.repositories;

import io.leedsk1y.todolist_backend.models.ERole;
import io.leedsk1y.todolist_backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(ERole name);
}