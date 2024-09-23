package org.tbank.repository;

import org.springframework.stereotype.Component;
import org.tbank.model.Identifiable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class SimpleRepository<T extends Identifiable<Long>> {

    private final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement()); // Устанавливаею уникальный ID
        }
        storage.put(entity.getId(), entity);
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public void deleteAll() {
        storage.clear();
    }
}
