package org.tbank.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Identifiable<Long>, BaseEntity {
    private Long id;
    private Long cityId;
    private String slug;
    private String name;
}
