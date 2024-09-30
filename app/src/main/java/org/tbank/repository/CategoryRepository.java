package org.tbank.repository;

import org.springframework.stereotype.Repository;
import org.tbank.model.Category;
import java.util.Optional;


@Repository
public class CategoryRepository extends SimpleRepository<Category> {

    public Optional<Category> findBySlug(String slug) {
        return storage.values().stream()
                .filter(entity -> entity.getSlug() != null && entity.getSlug().equals(slug))
                .findFirst();
    }
}
