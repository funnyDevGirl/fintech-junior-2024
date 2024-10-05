package org.tbank.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Identifiable<Long>, BaseEntity {
    private Long id;
    private Long cityId;

    @NotBlank
    private String slug;

    private String name;
}
