package io.github.hugo1307.fallgates.data.cache;

import io.github.hugo1307.fallgates.confirmations.PluginConfirmation;
import io.github.hugo1307.fallgates.data.domain.FallGateSchematic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

@Getter
public class CacheKey {

    private Player player;
    private KeyType keyType;

    public static CacheKey createKey(KeyType keyType, Player player) {
        CacheKey cacheKey = new CacheKey();
        cacheKey.keyType = keyType;
        cacheKey.player = player;
        return cacheKey;
    }

    @Getter
    @RequiredArgsConstructor
    public enum KeyType {
        /**
         * Key for the confirmation of an operation in the cache.
         */
        CONFIRM_OPERATION(PluginConfirmation.class),
        /**
         * Key for the position of a gate in the cache.
         */
        GATE_BUILD_POSITION(Location.class),
        /**
         * Key for the schematic of a gate in the cache.
         */
        GATE_BUILD_SCHEMATIC(FallGateSchematic.class);

        private final Class<?> valueType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return Objects.equals(player, cacheKey.player) && keyType == cacheKey.keyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, keyType);
    }
}
