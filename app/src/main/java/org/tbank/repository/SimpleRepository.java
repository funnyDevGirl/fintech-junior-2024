package org.tbank.repository;

import lombok.extern.slf4j.Slf4j;
import org.tbank.annotation.LogExecutionTime;
import org.tbank.dto.categories.CategorySnapshot;
import org.tbank.dto.locations.LocationSnapshot;
import org.tbank.model.Category;
import org.tbank.model.Identifiable;
import org.tbank.model.Location;
import org.tbank.observers.Observer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
abstract class SimpleRepository<T extends Identifiable<Long>, S> {

    final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final List<Observer<T>> observers = new ArrayList<>();
    private final List<S> snapshots = new ArrayList<>();

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


    public void saveSnapshot(S snapshot) {
        snapshots.add(snapshot);
        log.info("Snapshot saved: {}", snapshot);
    }

    public List<S> getSnapshots() {
        log.info("{} images were received", snapshots.size());
        return new ArrayList<>(snapshots);
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

            snapshots.removeIf(snapshot -> isSnapshotOfEntity(snapshot, removedEntity));
        } else {
            log.warn("No entity found with ID {} to delete.", id);
        }
    }

    private boolean isSnapshotOfEntity(S snapshot, T entity) {
        if (snapshot instanceof CategorySnapshot && entity instanceof Category) {
            return ((CategorySnapshot) snapshot).getId().equals(entity.getId());
        }
        else if (snapshot instanceof LocationSnapshot && entity instanceof Location) {
            return ((LocationSnapshot) snapshot).getId().equals(entity.getId());
        }
        log.info("Objects {} and {} have incompatible data types", snapshot, entity);
        return false;
    }

    public void deleteAll() {
        storage.clear();
        snapshots.clear();
        notifyObserversOnAllEntitiesDeleted();
    }
}
