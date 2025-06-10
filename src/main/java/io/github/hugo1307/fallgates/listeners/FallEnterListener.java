package io.github.hugo1307.fallgates.listeners;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.services.FallService;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class FallEnterListener implements Listener {

    private final FallGates plugin;
    private final FallService fallService;

    @Inject
    public FallEnterListener(FallGates plugin, FallService fallService) {
        this.plugin = plugin;
        this.fallService = fallService;
    }

    @EventHandler
    public void onFallEnter(PlayerMoveEvent event) {
        // The player must be moving downwards to enter a fall
        if (event.getTo() == null || event.getFrom().getY() < event.getTo().getY()) {
            return;
        }

        Player player = event.getPlayer();
        fallService.getOpenFalls().forEach(openFall -> {
            if (openFall.isInside(event.getTo()) && openFall.isConnected() && openFall.getPosition().getY() - event.getTo().getY() >= 5) {
                fallService.getFallById(openFall.getTargetFallId()).ifPresent(targetFall -> {
                    Location targetLocation = targetFall.getPosition().toBukkitLocation().clone();
                    targetLocation.add(0, -5, 0);
                    targetLocation.setYaw(event.getTo().getYaw());
                    targetLocation.setPitch(event.getTo().getPitch());
                    player.teleport(targetLocation);
                    player.setVelocity(new Vector(0, 1.5, 0.25));
                });
            }
        });
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Remove the teleporting flag when the player teleports
        event.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(plugin, "teleporting"));
    }

}
