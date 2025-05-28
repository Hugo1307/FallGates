package io.github.hugo1307.fallgates.data.models;

import io.github.hugo1307.fallgates.data.domain.DomainEntity;

public interface DataModel<T, C extends DomainEntity<?>> {

    /**
     * Get the id of the entity
     *
     * @return the id of the entity.
     */
    T getId();

    /**
     * Convert data entity to domain entity
     *
     * <p>
     * Converts a data entity to a domain entity - which is used by the logic of the plugin.
     * These logic entities allow the necessary decoupling between the database data and the plugin's logic.
     *
     * @return the domain entity.
     */
    C toDomainEntity();

}
