package io.github.hugo1307.fallgates.items;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an item created and used by the plugin.
 */
public interface PluginItem {

    /**
     * Builds an {@link ItemStack} using the item as blueprint.
     *
     * @return the built {@link ItemStack}
     */
    ItemStack buildItem();

    /**
     * Called when the item is used.
     *
     * <p>This method should contain the logic that should be executed when the item is used.
     *
     * @param player       the player who clicked the item
     * @param action       the action that triggered the click
     * @param blockClicked the block that was clicked, if applicable
     */
    void onUse(Player player, Action action, Block blockClicked);

    /**
     * Give the item to the specified player.
     *
     * @param player the player to give the item to
     */
    default void give(Player player) {
        ItemStack itemStack = buildItem();
        if (itemStack != null && !itemStack.getType().isAir()) {
            player.getInventory().addItem(itemStack);
        } else {
            throw new IllegalStateException("Failed to build item for player: " + player.getName());
        }
    }
}
