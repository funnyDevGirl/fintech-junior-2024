package org.tbank.dto.locations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.tbank.model.Location;

@Getter
@Setter
@AllArgsConstructor
public class LocationSnapshot {
    private final Location location;
    private final Long id;
    private final String slug;
    private final String name;

    public void restore() {
        location.setId(id);
        location.setSlug(slug);
        location.setName(name);
    }
}
