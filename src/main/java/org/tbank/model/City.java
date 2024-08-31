package org.tbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;


@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class City {

    private String slug;

    @JsonProperty("coords")
    @ToString.Include(name = "coords")
    private Coordinates coordinates;


    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Coordinates {

        @JsonProperty("lat")
        @ToString.Include(name = "lat")
        private double latitude;

        @JsonProperty("lon")
        @ToString.Include(name = "lon")
        private double longitude;
    }
}
