package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.*;
import dev.hugog.minecraft.dev_command.arguments.parsers.StringArgumentParser;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.data.domain.FallGateSchematic;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.FallService;
import io.github.hugo1307.fallgates.services.SchematicsService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@AutoValidation
@Command(alias = "delete", description = "Delete an existing fall.", permission = "fallgates.command.delete", isPlayerOnly = true)
@Arguments({
        @Argument(name = "id", description = "The ID of the fall to delete.", position = 0, parser = StringArgumentParser.class)
})
@Dependencies(dependencies = {FallGates.class, ServiceAccessor.class})
public class FallDeleteCommand extends BukkitDevCommand {

    private final FallService fallService;
    private final SchematicsService schematicsService;
    private final MessageService messageService;

    public FallDeleteCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);
        ServiceAccessor serviceAccessor = getDependency(ServiceAccessor.class);
        this.fallService = serviceAccessor.accessService(FallService.class);
        this.schematicsService = serviceAccessor.accessService(SchematicsService.class);
        this.messageService = serviceAccessor.accessService(MessageService.class);
    }

    @Override
    public void execute() {
        String fallId = (String) getArgumentParser(0).parse().orElseThrow();
        Player player = (Player) getCommandSender();

        if (!fallService.exists(fallId)) {
            messageService.sendMessage(player, Message.FALL_NOT_FOUND, fallId);
            return;
        }

        Fall fallToDelete = fallService.getFallById(fallId).orElseThrow();
        fallService.deleteFall(fallToDelete);

        // If the backup is available, restore the terrain schematic
        if (schematicsService.isBackupAvailable(fallToDelete.getName())) {
            Path terrainBackupPath = schematicsService.getTerrainBackupPath(fallToDelete.getName());
            FallGateSchematic terrainSchematic = schematicsService.loadSchematic(terrainBackupPath);

            schematicsService.pasteSchematic(terrainSchematic, fallToDelete.getPosition().toBukkitLocation());
            schematicsService.deleteSchematic(terrainBackupPath);
        }

        messageService.sendMessage(player, Message.FALL_DELETE_SUCCESS, fallToDelete.getName());
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 1) {
            return fallService.getAllFalls().stream()
                    .map(Fall::getId)
                    .collect(Collectors.toUnmodifiableList());
        }
        return List.of();
    }

}
