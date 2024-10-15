package org.tbank.dto.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private int count;
    private String next;
    private String previous;

    @JsonProperty("results")
    private List<EventDTO> events;
}
