package org.tbank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @CreatedDate
    private LocalDate createdAt;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.MERGE)
    private Set<User> users = new HashSet<>();

    public void addUser(User user) {
        this.getUsers().add(user);
        user.getRoles().add(this);
    }

    public void removeUser(User user) {
        this.getUsers().remove(user);
        user.getRoles().remove(this);
    }
}
