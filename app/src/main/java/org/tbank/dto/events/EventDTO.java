package org.tbank.dto.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String priceTextValue;  // временный тип String для парсинга

    @JsonIgnore
    private Price minPrice; // получаю из класса Parser parsePrice(priceTextValue)
}
