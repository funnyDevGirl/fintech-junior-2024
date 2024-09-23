package org.tbank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Identifiable<Long>, BaseEntity {
    private Long id;
    private String slug;
    private String name;
}
