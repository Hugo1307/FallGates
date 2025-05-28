package io.github.hugo1307.fallgates.listeners;

import io.github.hugo1307.fallgates.items.PluginItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PluginItemUseListener implements Listener {

    private static final PluginItem[] PLUGIN_ITEMS = {

    };

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        ItemStack itemInHand = event.getItem();
        if (itemInHand == null) {
            return;
        }


        Arrays.stream(PLUGIN_ITEMS).forEach(pluginItem -> {
            if (itemInHand.isSimilar(pluginItem.buildItem())) {
                pluginItem.onUse(event.getPlayer(), event.getAction(), event.getClickedBlock());
                event.setCancelled(true); // Prevent further processing of the event
            }
        });

    }

}
