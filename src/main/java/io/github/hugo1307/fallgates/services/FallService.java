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
     * Get a Fall by its ID from the cache.
     *
     * @param id the ID of the Fall to retrieve
     * @return an Optional containing the Fall if found, or empty if not found
     */
    public Optional<Fall> getFallById(Long id) {
        return Optional.ofNullable(fallsCache.get(id));
    }

    /**
     * Check if a Fall exists by its ID.
     *
     * @param id the ID of the Fall to check
     * @return true if the Fall exists, false otherwise
     */
    public boolean exists(Long id) {
        return fallsCache.contains(id);
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
     * Connect two Falls together, ensuring that neither is already connected to another Fall.
     *
     * @param sourceFall the first Fall to connect
     * @param targetFall the second Fall to connect
     * @throws IllegalStateException if either Fall is already connected to another Fall
     */
    public void connectFalls(Fall sourceFall, Fall targetFall) {
        if (sourceFall.getTargetFallId() != null || targetFall.getTargetFallId() != null) {
            throw new IllegalStateException("One of the falls is already connected to another fall.");
        }

        sourceFall.setTargetFallId(targetFall.getId());
        targetFall.setTargetFallId(sourceFall.getId());

        updateFall(sourceFall);
        updateFall(targetFall);
    }

    /**
     * Open the provided Fall by replacing blocks of the specified material within its radius with air.
     *
     * @param fall the Fall to open
     */
    public void openFall(Fall fall) {
        if (!fall.isOpen() && fall.isConnected()) {
            replaceFallBlocks(fall, fall.getMaterial(), Material.AIR);
            fall.setOpen(true);
            getFallById(fall.getTargetFallId()).ifPresent(this::openFall);
        }
    }

    /**
     * Close the provided Fall by replacing air blocks within its radius with the original material.
     *
     * @param fall the Fall to close
     */
    public void closeFall(Fall fall) {
        if (fall.isOpen()) {
            replaceFallBlocks(fall, Material.AIR, fall.getMaterial());
            fall.setOpen(false);

            getFallById(fall.getTargetFallId()).ifPresent(this::closeFall);
        }
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
     * Update an existing Fall in the database and cache.
     *
     * @param fall the Fall to update
     * @throws IllegalArgumentException if the Fall does not exist in the cache
     */
    public void updateFall(Fall fall) {
        if (!fallsCache.contains(fall.getId())) {
            throw new IllegalArgumentException("Fall with ID " + fall.getId() + " does not exist in the cache.");
        }
        fallRepository.update(fall.toDataModel())
                .thenRun(() -> fallsCache.put(fall));
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
