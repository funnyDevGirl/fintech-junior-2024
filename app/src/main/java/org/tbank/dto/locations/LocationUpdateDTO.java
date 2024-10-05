package org.tbank.dto.locations;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;


@Getter
@Setter
public class LocationUpdateDTO {
    @NotBlank
    private JsonNullable<String> slug;

    private JsonNullable<String> name;
}
