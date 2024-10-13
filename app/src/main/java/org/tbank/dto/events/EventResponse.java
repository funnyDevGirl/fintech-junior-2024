package org.tbank.dto.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private int count;
    private String next;
    private String previous;

    @JsonProperty("results")
    private List<EventDTO> events;
}
