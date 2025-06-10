package io.github.hugo1307.fallgates.data.cache;

import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.data.domain.Fall;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class OpenFallsCache {

    private final Set<Fall> openFalls;

    public OpenFallsCache() {
        this.openFalls = new HashSet<>();
    }

    /**
     * Add a fall to the cache.
     *
     * @param fall the Fall object to add to the cache
     */
    public void add(Fall fall) {
        openFalls.add(fall);
    }

    /**
     * Removes a fall from the cache.
     *
     * @param fall the Fall object to remove from the cache
     */
    public void remove(Fall fall) {
        openFalls.remove(fall);
    }

    /**
     * Gets all open falls from the cache.
     *
     * @return an unmodifiable set of all open falls
     */
    public Set<Fall> getOpenFalls() {
        return Collections.unmodifiableSet(openFalls);
    }

}
