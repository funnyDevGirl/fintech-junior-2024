package org.tbank.dto.locations;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;


@Getter
@Setter
public class LocationUpdateDTO {
    private JsonNullable<String> slug;
    private JsonNullable<String> name;
}
