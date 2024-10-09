package org.tbank.dto.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateDTO {

    @NotBlank
    private String slug;

    @JsonProperty("id")
    private Long cityId;

    private String name;
}
