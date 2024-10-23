package org.tbank.dto.locations;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import java.util.Set;

@Getter
@Setter
public class LocationUpdateDTO {
    @NotBlank
    private JsonNullable<String> slug;

    @NotBlank
    private JsonNullable<String> name;

    private JsonNullable<Set<Long>> eventIds;
}
