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
    private String title;
    private String slug;
    private Location location;

    @JsonProperty("price")
    private String priceTextValue;  // временный тип String для парсинга

    private Price price; // получаю из класса Parser parsePrice(priceTextValue)
}
