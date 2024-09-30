package org.tbank.dto.categories;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;


@Getter
@Setter
public class CategoryUpdateDTO {
    private JsonNullable<String> slug;
    private JsonNullable<Long> cityId;
    private JsonNullable<String> name;
}
