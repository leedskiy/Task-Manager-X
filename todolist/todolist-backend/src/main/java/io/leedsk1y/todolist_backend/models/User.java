package io.leedsk1y.todolist_backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String profileImage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER) // Fetch roles eagerly to avoid LazyInitializationException
    @JoinTable(
            name = "user_roles",  // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"),  // User foreign key
            inverseJoinColumns = @JoinColumn(name = "role_id")  // Role foreign key
    )
    private Set<Role> roles = new HashSet<>();
}
