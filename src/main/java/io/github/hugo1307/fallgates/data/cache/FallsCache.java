package io.github.hugo1307.fallgates.data.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.data.models.FallModel;
import io.github.hugo1307.fallgates.data.repositories.FallRepository;
import lombok.NonNull;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class FallsCache {

    private final LoadingCache<String, Fall> cache;

    @Inject
    public FallsCache(FallRepository fallRepository) {
        this.cache = CacheBuilder.newBuilder()
                .build(new CacheLoader<>() {
                    @Override
                    @NonNull
                    public Fall load(@NonNull String id) {
                        return fallRepository.findById(id, FallModel.class).join()
                                .map(FallModel::toDomainEntity)
                                .orElseThrow(() -> new IllegalArgumentException("Fall not found"));
                    }
                });
    }

    /**
     * Retrieves a Fall from the cache.
     *
     * @param id the ID of the Fall to retrieve
     * @return the Fall object associated with the given ID
     */
    public Fall get(String id) {
        try {
            return cache.get(id);
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * Get all Falls from the cache.
     *
     * @return a set containing all Falls in the cache
     */
    public Set<Fall> getAll() {
        return Set.copyOf(cache.asMap().values());
    }

    /**
     * Puts a Fall into the cache.
     *
     * @param fall the Fall object to put into the cache
     */
    public void put(Fall fall) {
        cache.put(fall.getId(), fall);
    }

    /**
     * Puts all Falls into the cache.
     *
     * @param falls a set of Fall objects to put into the cache
     */
    public void putAll(Set<Fall> falls) {
        cache.putAll(falls.stream().collect(Collectors.toMap(Fall::getId, Function.identity())));
    }

    /**
     * Checks if a Fall with the given ID exists in the cache.
     *
     * @param id the ID of the Fall to check
     * @return true if the Fall exists in the cache, false otherwise
     */
    public boolean contains(String id) {
        return cache.asMap().containsKey(id);
    }

    /**
     * Invalidates a Fall in the cache by its ID.
     *
     * @param id the ID of the Fall to invalidate
     */
    public void invalidate(String id) {
        cache.invalidate(id);
    }

}
