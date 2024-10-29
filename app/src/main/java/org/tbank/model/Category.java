package org.tbank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tbank.dto.categories.CategorySnapshot;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Identifiable<Long>, BaseEntity {
    private Long id;
    private Long cityId;
    private String slug;
    private String name;

    public CategorySnapshot createSnapshot() {
        return new CategorySnapshot(this, id, cityId, slug, name);
    }
}
