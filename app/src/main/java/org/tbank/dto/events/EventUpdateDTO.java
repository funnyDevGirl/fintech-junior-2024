package org.tbank.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class EventUpdateDTO {
    @NotBlank(message = "Name cannot be blank")
    private JsonNullable<String> name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private JsonNullable<String> date;

    @NotNull(message = "Place id cannot be null")
    @JsonProperty("place_id")
    private JsonNullable<Long> placeId;
}
