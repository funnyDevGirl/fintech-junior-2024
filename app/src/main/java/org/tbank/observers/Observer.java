package org.tbank.observers;

public interface Observer<T> {

    void onEntitySaved(T entity);

    void onEntityDeleted(Long id);

    void onAllEntitiesDeleted();
}
