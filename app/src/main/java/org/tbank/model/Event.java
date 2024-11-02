package org.tbank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tbank.dto.events.Price;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event implements Identifiable<Long>, BaseEntity {
    private Long id;
    private String title;
    private String slug;
    private Location location;
    private Price price;
}
