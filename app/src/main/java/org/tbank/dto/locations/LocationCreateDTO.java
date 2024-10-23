package org.tbank.dto.locations;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationCreateDTO {
    @NotBlank
    private String slug;

    @NotBlank
    private String name;

    private Set<Long> eventIds = new HashSet<>();
}
