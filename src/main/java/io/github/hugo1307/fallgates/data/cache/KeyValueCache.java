package io.github.hugo1307.fallgates.data.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;

import java.util.concurrent.TimeUnit;

@Singleton
public class KeyValueCache {

    private final Cache<CacheKey, Object> cache;

    public KeyValueCache() {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    /**
     * Retrieves an element from the cache.
     *
     * @param key the key of the element to retrieve
     * @return the value associated with the key, or null if not found
     */
    public Object get(CacheKey key) {
        return cache.getIfPresent(key);
    }

    /**
     * Adds an element to the cache.
     *
     * @param key   the key of the element
     * @param value the value of the element
     */
    public void add(CacheKey key, Object value) {
        cache.put(key, value);
    }

    /**
     * Removes an element from the cache.
     *
     * @param key the key of the element to remove
     */
    public void remove(CacheKey key) {
        cache.invalidate(key);
    }

    /**
     * Checks if the cache contains an element with the specified key.
     *
     * @param key the key to check
     * @return true if the cache contains the key, false otherwise
     */
    public boolean contains(CacheKey key) {
        return cache.getIfPresent(key) != null;
    }

}
