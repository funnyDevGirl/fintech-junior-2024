package org.tbank.dto.categories;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private Long cityId;
    private String slug;
    private String name;
}
