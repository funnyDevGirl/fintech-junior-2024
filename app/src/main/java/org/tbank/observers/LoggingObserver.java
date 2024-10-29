package org.tbank.observers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingObserver<T> implements Observer<T> {

    @Override
    public void onEntitySaved(T entity) {
        log.info("Entity '{}' is saved in the storage", entity);
    }

    @Override
    public void onEntityDeleted(Long id) {
        log.info("Entity with id '{}' was deleted from the storage", id);
    }

    @Override
    public void onAllEntitiesDeleted() {
        log.info("All entities was deleted from the storage");
    }
}
