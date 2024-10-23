package org.tbank.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.tbank.dto.events.EventFilterDTO;
import org.tbank.model.Event;
import java.time.LocalDate;

@Component
public class EventSpecification {


    public Specification<Event> build(EventFilterDTO filters) {
        return withPlace(filters.getPlaceId())
                .and(withName(filters.getName()))
                .and(withDateRange(filters.getFromDate(), filters.getToDate()));
    }

    private Specification<Event> withPlace(Long placeId) {
        return (root, query, cb) -> {
            if (placeId == null) {
                return cb.conjunction();
            }
            // JOIN FETCH для жадной загрузки
            root.fetch("place"); // поле в классе Event
            return cb.equal(root.get("place").get("id"), placeId);
        };
    }

    private Specification<Event> withName(String name) {
        return (root, query, cb) -> (name == null || name.isEmpty())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private Specification<Event> withDateRange(LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) {
                return cb.conjunction();
            }
            Predicate predicate = cb.conjunction();

            if (fromDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("date"), fromDate));
            }
            if (toDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("date"), toDate));
            }

            return predicate;
        };
    }
}
