package io.github.hugo1307.fallgates.items;

import io.github.hugo1307.fallgates.data.cache.CacheKey;
import io.github.hugo1307.fallgates.data.cache.KeyValueCache;
import io.github.hugo1307.fallgates.data.domain.FallGateSchematic;
import io.github.hugo1307.fallgates.services.SchematicsService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Item used to set the position of a gate during its construction.
 */
public class GateBuildPositionSelectorItem implements PluginItem {

    private final KeyValueCache keyValueCache;
    private final SchematicsService schematicsService;

    public GateBuildPositionSelectorItem(KeyValueCache keyValueCache, SchematicsService schematicsService) {
        this.keyValueCache = keyValueCache;
        this.schematicsService = schematicsService;
    }

    @Override
    public ItemStack buildItem() {
        ItemStack itemStack = new ItemStack(Material.STICK, 1);
        if (itemStack.getItemMeta() == null) {
            throw new IllegalStateException("ItemStack meta cannot be null");
        }

        itemStack.getItemMeta().setDisplayName("§aGate Build Tool");
        itemStack.getItemMeta().setLore(List.of("§a[Left-click] §7to select the position."));
        itemStack.getItemMeta().setLore(List.of("§a[Right-click] §7to confirm the position."));
        return itemStack;
    }

    @Override
    public void onUse(Player player, Action action, Block blockClicked) {
        if (action == Action.LEFT_CLICK_BLOCK) {
            keyValueCache.add(CacheKey.createKey(CacheKey.KeyType.GATE_BUILD_POSITION, player), blockClicked.getLocation());
            player.sendMessage("§aPosition selected! Now right-click to confirm.");
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            Location selectedPosition = (Location) keyValueCache.get(CacheKey.createKey(CacheKey.KeyType.GATE_BUILD_POSITION, player));
            if (selectedPosition != null) {
                schematicsService.pasteSchematic(
                        (FallGateSchematic) keyValueCache.get(CacheKey.createKey(CacheKey.KeyType.GATE_BUILD_SCHEMATIC, player)),
                        selectedPosition
                );
                player.sendMessage("§aPosition confirmed! You can now build the gate.");
            } else {
                player.sendMessage("§cNo position selected! Please left-click to select a position first.");
            }
        }
    }

}
