package org.tbank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "places")
@EntityListeners(AuditingEntityListener.class)
@ToString
@Getter
@Setter
@EqualsAndHashCode(of = "slug")
@AllArgsConstructor
@NoArgsConstructor
public class Location implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    private String slug;

    @NotBlank
    private String name;

    @CreatedDate
    private LocalDate createdAt;

    @OneToMany(mappedBy = "place", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();

    public void addEvent(Event event) {
        this.getEvents().add(event);
        event.setPlace(this);
    }

    public void removeEvent(Event event) {
        this.getEvents().remove(event);
        event.setPlace(null);
    }
}
