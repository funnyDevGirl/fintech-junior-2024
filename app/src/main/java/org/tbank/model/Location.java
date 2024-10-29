package org.tbank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tbank.dto.locations.LocationSnapshot;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Identifiable<Long>, BaseEntity {
    private Long id;
    private String slug;
    private String name;

    public LocationSnapshot createSnapshot() {
        return new LocationSnapshot(this, id, slug, name);
    }
}
