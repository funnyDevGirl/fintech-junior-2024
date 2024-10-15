package org.tbank.dto.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tbank.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("price")
    private String price;

    public EventDTO(String title, String slug, Location location, String price) {
        this.title = title;
        this.slug = slug;
        this.location = location;
        this.price = price;
    }
}
