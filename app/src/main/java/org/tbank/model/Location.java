package org.tbank.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Identifiable<Long>, BaseEntity {
    private Long id;

    @NotBlank
    private String slug;

    private String name;
}
