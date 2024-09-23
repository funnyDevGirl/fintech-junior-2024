package org.tbank.repository;

import org.springframework.stereotype.Component;
import org.tbank.model.Category;

@Component
public class CategoryRepository extends SimpleRepository<Category> {
    // можно добавить доп. методы, специфичные для Category
}
