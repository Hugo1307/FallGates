package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.confirmations.PluginConfirmation;
import io.github.hugo1307.fallgates.data.cache.CacheKey;
import io.github.hugo1307.fallgates.data.cache.KeyValueCache;
import org.bukkit.entity.Player;

import java.util.Optional;

@Singleton
public class ConfirmationService implements Service {

    private final KeyValueCache keyValueCache;

    @Inject
    public ConfirmationService(KeyValueCache keyValueCache) {
        this.keyValueCache = keyValueCache;
    }

    /**
     * Adds a confirmation for a player to the cache.
     *
     * @param player the player for whom the confirmation is being added
     */
    public void addConfirmation(Player player, PluginConfirmation confirmation) {
        keyValueCache.add(CacheKey.createKey(CacheKey.KeyType.CONFIRM_OPERATION, player), confirmation);
    }

    /**
     * Removes a confirmation for a player from the cache.
     *
     * @param player the player for whom the confirmation is being removed
     */
    public void removeConfirmation(Player player) {
        keyValueCache.remove(CacheKey.createKey(CacheKey.KeyType.CONFIRM_OPERATION, player));
    }

    /**
     * Checks if a player has a confirmation pending in the cache.
     *
     * @param player the player to check for a confirmation
     * @return true if the player has a confirmation pending, false otherwise
     */
    public boolean hasConfirmation(Player player) {
        return keyValueCache.contains(CacheKey.createKey(CacheKey.KeyType.CONFIRM_OPERATION, player));
    }

    /**
     * Retrieves the confirmation for a player from the cache.
     *
     * @param player the player whose confirmation is being retrieved
     * @return an Optional containing the PluginConfirmation if present, or empty if not
     */
    public Optional<PluginConfirmation> getConfirmation(Player player) {
        Object confirmation = keyValueCache.get(CacheKey.createKey(CacheKey.KeyType.CONFIRM_OPERATION, player));
        if (confirmation instanceof PluginConfirmation) {
            return Optional.of((PluginConfirmation) confirmation);
        }
        return Optional.empty();
    }

}
