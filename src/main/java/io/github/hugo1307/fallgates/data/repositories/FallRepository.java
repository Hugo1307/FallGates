package io.github.hugo1307.fallgates.data.repositories;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.data.HibernateHandler;
import io.github.hugo1307.fallgates.data.models.FallModel;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FallRepository extends AbstractRepository<String, FallModel> {

    @Inject
    public FallRepository(HibernateHandler dataHandler) {
        super(dataHandler);
    }
    
    public CompletableFuture<List<FallModel>> findAll() {
        return fetchListQuery("FROM FallModel", Collections.emptyList(), FallModel.class);
    }

}
