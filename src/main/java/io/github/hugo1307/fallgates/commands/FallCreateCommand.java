package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.*;
import dev.hugog.minecraft.dev_command.arguments.parsers.StringArgumentParser;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import io.github.hugo1307.fallgates.data.domain.FallGateSchematic;
import io.github.hugo1307.fallgates.services.GateService;
import io.github.hugo1307.fallgates.services.SchematicsService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@AutoValidation
@Command(alias = "create", description = "Create a new fall gate.", permission = "fallgates.command.create", isPlayerOnly = true)
@Arguments({
        @Argument(name = "name", description = "The name of the fall gate.", position = 0, parser = StringArgumentParser.class),
        @Argument(name = "schematicName", description = "The name of the schematic to use.", position = 1, parser = StringArgumentParser.class)
})
@Dependencies(dependencies = {SchematicsService.class})
public class FallCreateCommand extends BukkitDevCommand {

    public FallCreateCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);
    }

    @Override
    public void execute() {

        SchematicsService schematicsService = getDependency(SchematicsService.class);
        GateService gateService = getDependency(GateService.class);
        Player player = (Player) getCommandSender();
        String fallGateName = ((StringArgumentParser) getArgumentParser(0)).parse().orElseThrow();
        String schematicName = ((StringArgumentParser) getArgumentParser(1)).parse().orElseThrow();

        FallGateSchematic fallGateSchematic = schematicsService.loadSchematic(schematicName);
        schematicsService.pasteSchematic(fallGateSchematic, player.getLocation());
    }

    @Override
    public List<String> onTabComplete(String[] strings) {
        return List.of();
    }
}
