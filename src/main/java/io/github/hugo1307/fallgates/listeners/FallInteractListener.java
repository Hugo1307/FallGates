package io.github.hugo1307.fallgates.listeners;

import com.google.inject.Inject;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.services.FallService;
import org.bukkit.Location;
import org.bukkit.block.data.Powerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class FallInteractListener implements Listener {

    private final FallGates plugin;
    private final FallService fallService;

    @Inject
    public FallInteractListener(FallGates plugin, FallService fallService) {
        this.plugin = plugin;
        this.fallService = fallService;
    }

    @EventHandler
    public void onPressurePlateInteract(PlayerInteractEvent event) {
        // Ignore all interactions that are not physical (e.g., right-clicks with items)
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        // Check if the clicked block is a pressure plate or a pressure sensor
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getBlockData() instanceof Powerable)) {
            return;
        }

        Location pressurePlateLocation = event.getClickedBlock().getLocation();
        Optional<Fall> closestFallOptional = fallService.getClosestFall(pressurePlateLocation);
        if (closestFallOptional.isEmpty()) {
            return;
        }

        Fall closestFall = closestFallOptional.get();
        int closestFallMaxSize = Math.max(closestFall.getXSize(), closestFall.getZSize());
        // If the pressure plate is too far from the closest fall AND the pressure plate is not inside the closest fall, ignore
        if (!closestFall.isInside(pressurePlateLocation)
                && closestFall.getPosition().toBukkitLocation().distance(pressurePlateLocation) > closestFallMaxSize * 2) {
            return;
        }

        if (closestFall.getTargetFallId() == null) {
            plugin.getLogger().warning("Fall at " + closestFall.getPosition() + " does not have a target fall ID set.");
            return;
        }

        if (!fallService.exists(closestFall.getTargetFallId())) {
            plugin.getLogger().warning("Target fall with ID " + closestFall.getTargetFallId() + " does not exist for fall at " + closestFall.getPosition());
            return;
        }

        fallService.openFall(closestFall);
        fallService.scheduleFallClose(closestFall);

        // If the target fall exists, open it and schedule its close
        fallService.getFallById(closestFall.getTargetFallId()).ifPresent(targetFall -> {
            fallService.openFall(targetFall);
            fallService.scheduleFallClose(targetFall);
        });
    }

}
