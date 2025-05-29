package io.github.hugo1307.fallgates.data.repositories;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.data.HibernateHandler;
import io.github.hugo1307.fallgates.data.models.FallModel;

public class GateRepository extends AbstractRepository<Long, FallModel> {

    @Inject
    public GateRepository(HibernateHandler dataHandler) {
        super(dataHandler);
    }

}
