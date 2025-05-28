package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.AutoValidation;
import dev.hugog.minecraft.dev_command.annotations.Command;
import dev.hugog.minecraft.dev_command.annotations.Dependencies;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import io.github.hugo1307.fallgates.data.cache.CacheKey;
import io.github.hugo1307.fallgates.data.cache.KeyValueCache;
import io.github.hugo1307.fallgates.data.domain.FallGateSchematic;
import io.github.hugo1307.fallgates.services.SchematicsService;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@AutoValidation
@Command(alias = "confirm", description = "Confirms a operation in the plugin.", permission = "fallgates.command.confirm", isPlayerOnly = true)
@Dependencies(dependencies = {KeyValueCache.class, SchematicsService.class})
@SuppressWarnings("unused")
public class ConfirmationCommand extends BukkitDevCommand {

    public ConfirmationCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);
    }

    @Override
    public void execute() {
        KeyValueCache keyValueCache = getDependency(KeyValueCache.class);
        SchematicsService schematicsService = getDependency(SchematicsService.class);
        Player player = (Player) getCommandSender();

        if (!keyValueCache.contains(CacheKey.createKey(CacheKey.KeyType.CONFIRM_OPERATION, player))) {
            player.sendMessage("No confirmation pending for you.");
            return;
        }

        ConfirmationType confirmationType = (ConfirmationType) keyValueCache.get(CacheKey.createKey(CacheKey.KeyType.CONFIRM_OPERATION, player));
        switch (confirmationType) {
            case BUILD_GATE:
                schematicsService.pasteSchematic(
                        (FallGateSchematic) keyValueCache.get(CacheKey.createKey(CacheKey.KeyType.GATE_BUILD_SCHEMATIC, player)),
                        (Location) keyValueCache.get(CacheKey.createKey(CacheKey.KeyType.GATE_BUILD_POSITION, player))
                );
                player.sendMessage("Gate built successfully!");
        }


    }

    @Override
    public List<String> onTabComplete(String[] strings) {
        return List.of();
    }

    public enum ConfirmationType {
        BUILD_GATE
    }

}
