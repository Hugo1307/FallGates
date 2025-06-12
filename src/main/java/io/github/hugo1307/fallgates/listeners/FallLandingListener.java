package io.github.hugo1307.fallgates.listeners;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.services.TeleportService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class FallLandingListener implements Listener {

    private final TeleportService teleportService;

    @Inject
    public FallLandingListener(TeleportService teleportService) {
        this.teleportService = teleportService;
    }

    @EventHandler
    public void onPlayerLandWithDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        Player player = (Player) event.getEntity();
        // If the player is teleporting, cancel the damage event to prevent fall damage when leaving the fall
        if (teleportService.isTeleporting(player)) {
            teleportService.finishTeleport(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    @SuppressWarnings("deprecation") // isOnGround() method does not impose a security risk in this case
    public void onPlayerLandWithoutDamage(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (teleportService.isTeleporting(player) && player.isOnGround()) {
            teleportService.finishTeleport(player);
        }
    }

}
