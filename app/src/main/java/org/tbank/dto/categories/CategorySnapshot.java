package org.tbank.dto.categories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.tbank.model.Category;

@Getter
@Setter
@AllArgsConstructor
public class CategorySnapshot {
    private final Category category;
    private final Long id;
    private final Long cityId;
    private final String slug;
    private final String name;

    public void restore() {
        category.setId(id);
        category.setCityId(cityId);
        category.setSlug(slug);
        category.setName(name);
    }
}
