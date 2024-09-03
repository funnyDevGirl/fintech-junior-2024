package org.tbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {

    private String slug;

    @JsonProperty("coords")
    @ToString.Include(name = "coords")
    private Coordinates coordinates;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Coordinates {

        @JsonProperty("lat")
        @ToString.Include(name = "lat")
        private double latitude;

        @JsonProperty("lon")
        @ToString.Include(name = "lon")
        private double longitude;
    }
}
