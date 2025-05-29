package io.github.hugo1307.fallgates.confirmations;

import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Represents a confirmation for a plugin operation.
 *
 * <p>This interface can be extended to define specific confirmation behaviors.
 */
@RequiredArgsConstructor
public abstract class PluginConfirmation {

    protected final ConfirmationType confirmationType;
    protected final FallGates plugin;
    protected final ServiceAccessor serviceAccessor;

    /**
     * Handles the confirmation action for a player.
     *
     * @param player the player who confirmed the action
     */
    public abstract void onConfirm(Player player);

    /**
     * Represents the type of confirmation that can be performed.
     */
    public enum ConfirmationType {
        CREATE_FALL
    }

}
