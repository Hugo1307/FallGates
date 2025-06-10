package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.*;
import dev.hugog.minecraft.dev_command.arguments.parsers.IntegerArgumentParser;
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

@AutoValidation
@Command(alias = "delete", description = "Delete an existing fall.", permission = "fallgates.command.delete", isPlayerOnly = true)
@Arguments({
        @Argument(name = "id", description = "The ID of the fall to delete.", position = 0, parser = IntegerArgumentParser.class)
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
        long fallId = (int) getArgumentParser(0).parse().orElseThrow();
        Player player = (Player) getCommandSender();

        if (!fallService.exists(fallId)) {
            messageService.sendPlayerMessage(player, Message.FALL_NOT_FOUND, String.valueOf(fallId));
            return;
        }

        Fall fallToDelete = fallService.getFallById(fallId);

        fallService.deleteFall(fallToDelete);

        Path terrainBackupPath = schematicsService.getTerrainBackupPath(fallToDelete.getName());
        FallGateSchematic terrainSchematic = schematicsService.loadSchematic(terrainBackupPath);

        schematicsService.pasteSchematic(terrainSchematic, fallToDelete.getPosition().toBukkitLocation());
        schematicsService.deleteSchematic(terrainBackupPath);
    }

    @Override
    public List<String> onTabComplete(String[] strings) {
        return List.of();
    }

}
