package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.data.cache.FallsCache;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.data.models.FallModel;
import io.github.hugo1307.fallgates.data.repositories.FallRepository;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class FallService implements Service {

    private final FallRepository fallRepository;
    private final FallsCache fallsCache;

    @Inject
    public FallService(FallRepository fallRepository, FallsCache fallsCache) {
        this.fallRepository = fallRepository;
        this.fallsCache = fallsCache;
    }

    /**
     * Get the closest Fall to a given location within a specified radius.
     *
     * @param location the location to search from
     * @param radius   the radius within which to search for Falls
     * @return an Optional containing the closest Fall if found, or empty if no Falls are within the radius
     */
    public Optional<Fall> getClosestFall(Location location, int radius) {
        return fallsCache.getAll().stream()
                .filter(fall -> fall.getPosition().toBukkitLocation().distance(location) <= radius)
                .findFirst();
    }

    /**
     * Open the provided Fall by replacing blocks of the specified material within its radius with air.
     *
     * @param fall the Fall to open
     */
    public void openFall(Fall fall) {
        replaceFallBlocks(fall, fall.getMaterial(), Material.AIR);
        fall.setOpen(true);
    }

    /**
     * Close the provided Fall by replacing air blocks within its radius with the original material.
     *
     * @param fall the Fall to close
     */
    public void closeFall(Fall fall) {
        replaceFallBlocks(fall, Material.AIR, fall.getMaterial());
        fall.setOpen(false);
    }

    private void replaceFallBlocks(Fall fall, Material materialToReplace, Material replacementMaterial) {
        Location location = fall.getPosition().toBukkitLocation();
        int maximumXRadius = fall.getXSize() / 2;
        int maximumZRadius = fall.getZSize() / 2;

        if (location.getWorld() == null) {
            throw new IllegalArgumentException("Location world cannot be null");
        }

        for (int x = location.getBlockX() - maximumXRadius; x <= location.getBlockX() + maximumXRadius; x++) {
            for (int z = location.getBlockZ() - maximumZRadius; z <= location.getBlockZ() + maximumZRadius; z++) {
                Block blockAtLocation = location.getWorld().getBlockAt(x, location.getBlockY() - 1, z);
                if (blockAtLocation.getType() == materialToReplace) {
                    blockAtLocation.setType(replacementMaterial);
                }
            }
        }
    }

    /**
     * Load all Falls from the database into the cache.
     *
     * @return a CompletableFuture that completes when the loading is done
     */
    public CompletableFuture<Void> loadFalls() {
        return fallRepository.findAll().thenAccept(falls -> fallsCache.putAll(falls.stream()
                .map(FallModel::toDomainEntity)
                .collect(Collectors.toUnmodifiableSet())));
    }

    /**
     * Save a gate to the database.
     *
     * @param fall the gate to save
     */
    public void saveFall(Fall fall) {
        fallRepository.save(fall.toDataModel())
                .thenAccept(savedModel -> fallsCache.put(savedModel.toDomainEntity()));
    }

}
