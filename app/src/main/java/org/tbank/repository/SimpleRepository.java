package org.tbank.repository;

import lombok.extern.slf4j.Slf4j;
import org.tbank.annotation.LogExecutionTime;
import org.tbank.model.Identifiable;
import org.tbank.observers.Observer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
abstract class SimpleRepository<T extends Identifiable<Long>> {

    final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final List<Observer<T>> observers = new ArrayList<>();

    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    private void notifyObserversOnSave(T entity) {
        for (Observer<T> observer : observers) {
            observer.onEntitySaved(entity);
        }
    }

    private void notifyObserversOnDelete(Long id) {
        for (Observer<T> observer : observers) {
            observer.onEntityDeleted(id);
        }
    }
    private void notifyObserversOnAllEntitiesDeleted() {
        for (Observer<T> observer : observers) {
            observer.onAllEntitiesDeleted();
        }
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    // фактическая инициация хранилища в виде объекта ConcurrentHashMap происходит при создании SimpleRepository,
    // а заполнение — через метод save
    @LogExecutionTime
    public void save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement()); // Устанавливаю уникальный ID
        }
        storage.put(entity.getId(), entity);
        notifyObserversOnSave(entity);
    }

    public void deleteById(Long id) {
        T removedEntity = storage.remove(id);
        if (removedEntity != null) {
            log.info("Entity with ID {} deleted.", id);
            notifyObserversOnDelete(id);
        } else {
            log.warn("No entity found with ID {} to delete.", id);
        }
    }

    public void deleteAll() {
        storage.clear();
        notifyObserversOnAllEntitiesDeleted();
    }
}
