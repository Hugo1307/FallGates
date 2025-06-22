package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.config.ConfigHandler;
import io.github.hugo1307.fallgates.config.entities.FallsConfigEntry;
import io.github.hugo1307.fallgates.data.domain.Fall;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.*;

@Singleton
public class TeleportService implements Service {

    private final FallGates plugin;
    private final ConfigHandler configHandler;

    /**
     * Set to keep track of players who are currently teleporting.
     *
     * <p>The teleport is considered ended when the player has successfully landed in the target fall.
     */
    private static final Map<UUID, Long> teleportingPlayers = new HashMap<>();

    @Inject
    public TeleportService(FallGates plugin, ConfigHandler configHandler) {
        this.plugin = plugin;
        this.configHandler = configHandler;
    }

    /**
     * Teleports a player to the specified fall location.
     *
     * <p>The player will only be teleported if they are not already in the process of teleporting.
     *
     * @param player the player to teleport
     * @param fall   the fall to teleport the player to
     */
    public void teleportToFall(Player player, Fall fall) {
        Location currentLocation = player.getLocation();
        Location targetLocation = fall.getPosition().toBukkitLocation().clone();
        int fallHeight = configHandler.getValue(FallsConfigEntry.FALL_HEIGHT, Integer.class);

        targetLocation.add(0, -fallHeight, 0);
        targetLocation.setYaw(currentLocation.getYaw());
        targetLocation.setPitch(currentLocation.getPitch());

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            double verticalPushForce = configHandler.getValue(FallsConfigEntry.VERTICAL_FORCE, Double.class) * fallHeight;
            double horizontalPushForce = configHandler.getValue(FallsConfigEntry.HORIZONTAL_FORCE, Double.class) * (Math.max(3, fall.getZSize()) - 2);

            player.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setVelocity(new Vector(0, verticalPushForce, horizontalPushForce));
            teleportingPlayers.put(player.getUniqueId(), System.currentTimeMillis());
        });
    }

    /**
     * Finishes the teleportation process for a player.
     *
     * <p>The method is executed in a scheduled task to ensure it is only run on the next tick, and, avoid concurrency
     * issues between the several events that can finish the teleportation process.
     *
     * @param player the player whose teleportation is being finished
     */
    public void finishTeleport(Player player) {
        // Remove the player from the teleporting set when they finish teleporting
        plugin.getServer().getScheduler().runTask(plugin, () -> teleportingPlayers.remove(player.getUniqueId()));
    }

    /**
     * Checks if a player is currently teleporting.
     *
     * <p>The method also checks if the player has been teleporting for more than 10 seconds, and if so, it finishes the
     * teleportation process automatically.
     *
     * @param player the player to check
     * @return true if the player is currently teleporting, false otherwise
     */
    public boolean isTeleporting(Player player) {
        boolean hasKey = teleportingPlayers.containsKey(player.getUniqueId());
        // If the player has been teleporting for more than 10 seconds, remove them
        if (hasKey && teleportingPlayers.get(player.getUniqueId()) + 10000 < System.currentTimeMillis()) {
            finishTeleport(player);
            return false;
        }
        return hasKey;
    }

}
