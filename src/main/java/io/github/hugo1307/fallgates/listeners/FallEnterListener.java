package io.github.hugo1307.fallgates.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.data.domain.Fall;
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

    @Inject
    public FallEnterListener(FallService fallService, TeleportService teleportService) {
        this.fallService = fallService;
        this.teleportService = teleportService;
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
                Fall targetFall = fallService.getFallById(openFall.getTargetFallId());
                teleportService.teleportToFall(player, targetFall);
            }
        });
    }

}
