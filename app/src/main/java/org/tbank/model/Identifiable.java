package org.tbank.model;

public interface Identifiable<ID> {
    ID getId();
    void setId(ID id);
}
