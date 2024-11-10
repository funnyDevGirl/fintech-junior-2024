package org.tbank.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.tbank.enums.RoleType;
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

    public Role(RoleType roleType) {
        this.name = roleType.name();
    }
}
