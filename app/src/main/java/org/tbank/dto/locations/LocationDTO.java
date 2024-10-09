package org.tbank.dto.locations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class LocationDTO {
    private long id;
    private String slug;
    private String name;
}
