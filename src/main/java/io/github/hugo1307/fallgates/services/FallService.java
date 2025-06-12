package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.data.cache.FallsCache;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.data.models.FallModel;
import io.github.hugo1307.fallgates.data.repositories.FallRepository;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public final class FallService implements Service {

    private final FallRepository fallRepository;
    private final FallsCache fallsCache;

    private final Map<Fall, Long> fallsToClose = new HashMap<>();
    private final Set<Fall> openFalls = new HashSet<>();

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
    public Fall getFallById(Long id) {
        return fallsCache.get(id);
    }

    /**
     * Get all Falls from the cache.
     *
     * @return a Set containing all Falls
     */
    public Set<Fall> getAllFalls() {
        return Collections.unmodifiableSet(fallsCache.getAll());
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
     * @return an Optional containing the closest Fall if found, or empty if no Falls are within the radius
     */
    public Optional<Fall> getClosestFall(Location location) {
        return fallsCache.getAll().stream()
                .min(Comparator.comparingDouble(fall -> fall.getPosition().toBukkitLocation().distance(location)));
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
     * Get all Falls that are currently open.
     *
     * @return a Set of Falls that are open
     */
    public Set<Fall> getOpenFalls() {
        return Collections.unmodifiableSet(this.openFalls);
    }

    /**
     * Open the provided Fall by replacing blocks of the specified material within its radius with air.
     *
     * @param fall the Fall to open
     */
    public void openFall(Fall fall) {
        // No need to open if already open or not connected
        if (fall == null || fall.isOpen() || !fall.isConnected()) {
            return;
        }

        replaceFallBlocks(fall, fall.getMaterial(), Material.AIR);
        fall.setOpen(true);
        this.openFalls.add(fall);
    }

    /**
     * Close the provided Fall by replacing air blocks within its radius with the original material.
     *
     * @param fall the Fall to close
     */
    public void closeFallNow(Fall fall) {
        // No need to close if already closed
        if (!fall.isOpen()) {
            return;
        }

        // Close current fall
        replaceFallBlocks(fall, Material.AIR, fall.getMaterial());
        fall.setOpen(false);
        this.openFalls.remove(fall);
    }

    /**
     * Schedule a Fall to be closed after a certain period.
     *
     * @param fall the Fall to schedule for closing
     */
    public void scheduleFallClose(Fall fall) {
        fallsToClose.put(fall, System.currentTimeMillis());
    }

    /**
     * Process scheduled Fall closures, closing any Falls that have been scheduled for more than 5 seconds.
     */
    public void processClosingFalls() {
        long currentTime = System.currentTimeMillis();
        fallsToClose.entrySet().removeIf(entry -> {
            Fall fall = entry.getKey();
            long scheduledTime = entry.getValue();
            if (currentTime - scheduledTime >= 5000) { // 5 seconds
                closeFallNow(fall);
                return true;
            }
            return false;
        });
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

    /**
     * Delete a Fall from the database and invalidate it in the cache.
     *
     * @param fall the Fall to delete
     */
    public void deleteFall(Fall fall) {
        fallRepository.deleteById(fall.getId(), FallModel.class)
                .thenRun(() -> fallsCache.invalidate(fall.getId()));
    }

}
