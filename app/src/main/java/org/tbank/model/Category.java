package org.tbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Identifiable<Long>, BaseEntity {
    private Long id;

    @JsonProperty("city_id")
    private Long cityId;

    private String slug;
    private String name;

//    @CreatedDate
//    private LocalDate createdAt;
}
