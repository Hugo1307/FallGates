package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.*;
import dev.hugog.minecraft.dev_command.arguments.parsers.StringArgumentParser;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import io.github.hugo1307.fallgates.data.cache.CacheKey;
import io.github.hugo1307.fallgates.data.cache.KeyValueCache;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.SchematicsService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@AutoValidation
@Command(alias = "build", description = "Builds a new fall gate from a schematic.", permission = "fallgates.command.create", isPlayerOnly = true)
@Arguments({
        @Argument(name = "schematicName", description = "The name of the schematic to use.", position = 0, parser = StringArgumentParser.class)
})
@Dependencies(dependencies = {MessageService.class, SchematicsService.class, KeyValueCache.class})
@SuppressWarnings("unused")
public class FallBuildCommand extends BukkitDevCommand {

    public FallBuildCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);
    }

    @Override
    public void execute() {
        MessageService messageService = getDependency(MessageService.class);
        SchematicsService schematicsService = getDependency(SchematicsService.class);
        KeyValueCache keyValueCache = getDependency(KeyValueCache.class);
        Player player = (Player) getCommandSender();
        String schematicName = ((StringArgumentParser) getArgumentParser(0)).parse().orElseThrow();

        keyValueCache.add(CacheKey.createKey(CacheKey.KeyType.GATE_BUILD_SCHEMATIC, player), schematicsService.loadSchematic(schematicName));
        keyValueCache.add(CacheKey.createKey(CacheKey.KeyType.GATE_BUILD_POSITION, player), player.getLocation().clone().add(0, 1, 0));
        keyValueCache.add(CacheKey.createKey(CacheKey.KeyType.CONFIRM_OPERATION, player), ConfirmationCommand.ConfirmationType.BUILD_GATE);

        messageService.sendPlayerMessage(player, Message.GATE_BUILD_POSITION_SET, "/fg confirm");
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 1) {
            SchematicsService schematicsService = getDependency(SchematicsService.class);
            return schematicsService.getAvailableSchematicsNames();
        }
        return List.of();
    }
}
