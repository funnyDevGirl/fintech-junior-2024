package org.tbank.dto.categories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private Long cityId;
    private String slug;
    private String name;
}
