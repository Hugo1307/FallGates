package io.github.hugo1307.fallgates.data.domain;

import io.github.hugo1307.fallgates.data.models.DataModel;

public interface DomainEntity<T extends DataModel<?, ?>> {

    /**
     * Convert domain entity to data model.
     *
     * @return the data model representation of the entity.
     */
    T toDataModel();

}
