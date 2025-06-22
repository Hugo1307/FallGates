package io.github.hugo1307.fallgates.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.config.ConfigHandler;
import io.github.hugo1307.fallgates.config.entities.FallsConfigEntry;
import io.github.hugo1307.fallgates.services.FallService;
import io.github.hugo1307.fallgates.services.TeleportService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@Singleton
public class FallEnterListener implements Listener {

    private final FallService fallService;
    private final TeleportService teleportService;
    private final ConfigHandler configHandler;

    @Inject
    public FallEnterListener(FallService fallService, TeleportService teleportService, ConfigHandler configHandler) {
        this.fallService = fallService;
        this.teleportService = teleportService;
        this.configHandler = configHandler;
    }

    @EventHandler
    public void onFallEnter(PlayerMoveEvent event) {
        // The player must be moving downwards to enter a fall
        if (event.getTo() == null || event.getFrom().getY() < event.getTo().getY()) {
            return;
        }

        Player player = event.getPlayer();
        int fallHeight = configHandler.getValue(FallsConfigEntry.FALL_HEIGHT, Integer.class);
        fallService.getOpenFalls().forEach(openFall -> {
            if (openFall.isInside(event.getTo()) && openFall.isConnected() && openFall.getPosition().getY() - event.getTo().getY() >= fallHeight) {
                fallService.getFallById(openFall.getTargetFallId()).ifPresent(targetFall -> teleportService.teleportToFall(player, targetFall));
            }
        });
    }

}
