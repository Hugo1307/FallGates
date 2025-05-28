package io.github.hugo1307.fallgates.repositories;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.data.HibernateHandler;
import io.github.hugo1307.fallgates.data.models.GateModel;

public class GateRepository extends AbstractRepository<Long, GateModel> {

    @Inject
    public GateRepository(HibernateHandler dataHandler) {
        super(dataHandler);
    }

}
