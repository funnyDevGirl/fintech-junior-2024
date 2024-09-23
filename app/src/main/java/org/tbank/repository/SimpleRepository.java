package org.tbank.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.tbank.model.Identifiable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class SimpleRepository<T extends Identifiable<Long>> {

    final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private static final Logger logger = LoggerFactory.getLogger(SimpleRepository.class);


    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement()); // Устанавливаю уникальный ID
        }
        storage.put(entity.getId(), entity);
    }

    public void deleteById(Long id) {
        T removedEntity = storage.remove(id);
        if (removedEntity != null) {
            logger.info("Entity with ID {} deleted.", id);
        } else {
            logger.warn("No entity found with ID {} to delete.", id);
        }
    }

    public void deleteAll() {
        storage.clear();
    }
}
