package org.tbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
@ToString
@EqualsAndHashCode(of = {"name", "place"})
@Getter
@Setter
public class Event implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @ManyToOne
    @JoinColumn(name = "place_id")
    @NotNull
    private Location place;
    
    @NotNull
    private LocalDate date;

    @CreatedDate
    private LocalDate createdAt;
}
