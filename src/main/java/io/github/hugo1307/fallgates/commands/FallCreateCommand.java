package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.*;
import dev.hugog.minecraft.dev_command.arguments.parsers.IntegerArgumentParser;
import dev.hugog.minecraft.dev_command.arguments.parsers.StringArgumentParser;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.commands.parsers.MaterialArgumentParser;
import io.github.hugo1307.fallgates.confirmations.FallCreationConfirmation;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.data.domain.FallGateSchematic;
import io.github.hugo1307.fallgates.data.domain.Position;
import io.github.hugo1307.fallgates.exceptions.SchematicReadException;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.ConfirmationService;
import io.github.hugo1307.fallgates.services.SchematicsService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AutoValidation
@Command(alias = "create", description = "Create a new fall.", permission = "fallgates.command.create", isPlayerOnly = true)
@Arguments({
        @Argument(name = "name", description = "The name of the fall.", position = 0, parser = StringArgumentParser.class),
        @Argument(name = "schematicName", description = "The name of the schematic to use.", position = 1, parser = StringArgumentParser.class),
        @Argument(name = "material", description = "The material of the Fall.", position = 2, parser = MaterialArgumentParser.class),
        @Argument(name = "xSize", description = "The size of the fall on X axis.", position = 3, parser = IntegerArgumentParser.class, optional = true),
        @Argument(name = "zSize", description = "The size of the fall on Z axis.", position = 4, parser = IntegerArgumentParser.class, optional = true)
})
@Dependencies(dependencies = {FallGates.class, ServiceAccessor.class})
@SuppressWarnings("unused")
public class FallCreateCommand extends BukkitDevCommand {

    private final FallGates plugin;
    private final ServiceAccessor serviceAccessor;
    private final ConfirmationService confirmationService;
    private final MessageService messageService;
    private final SchematicsService schematicsService;

    private static final int DEFAULT_SIZE = 3;

    public FallCreateCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);

        this.plugin = getDependency(FallGates.class);
        this.serviceAccessor = getDependency(ServiceAccessor.class);
        this.confirmationService = this.serviceAccessor.accessService(ConfirmationService.class);
        this.messageService = this.serviceAccessor.accessService(MessageService.class);
        this.schematicsService = this.serviceAccessor.accessService(SchematicsService.class);
    }

    @Override
    public void execute() {
        Player player = (Player) getCommandSender();

        String fallName = ((StringArgumentParser) getArgumentParser(0)).parse().orElseThrow();
        String schematicName = ((StringArgumentParser) getArgumentParser(1)).parse().orElseThrow();
        Material material = ((MaterialArgumentParser) getArgumentParser(2)).parse().orElseThrow();
        int xSize = getOptionalArgumentParser(3)
                .map(parser -> ((IntegerArgumentParser) parser).parse().orElse(DEFAULT_SIZE))
                .orElse(DEFAULT_SIZE);
        int zSize = getOptionalArgumentParser(4)
                .map(parser -> ((IntegerArgumentParser) parser).parse().orElse(DEFAULT_SIZE))
                .orElse(DEFAULT_SIZE);
        Location schematicLocation = player.getLocation().clone().add(0, 1, 0);

        if (!schematicsService.isSchematicAvailable(schematicName)) {
            messageService.sendPlayerMessage(player, Message.FALL_CREATION_INVALID_SCHEMATIC, schematicName);
            return;
        }

        FallGateSchematic schematic;
        try {
            schematic = schematicsService.loadSchematic(schematicName);
        } catch (SchematicReadException e) {
            messageService.sendPlayerMessage(player, Message.FALL_CREATION_ERROR_LOADING_SCHEMATIC, schematicName);
            plugin.getLogger().severe("Failed to load schematic: Caused by: " + e.getCause());
            return;
        }

        Fall fallToCreate = new Fall(null, fallName, Position.fromBukkitLocation(schematicLocation), material, xSize, zSize);
        fallToCreate.setSchematic(schematic);

        FallCreationConfirmation fallCreationConfirmation = new FallCreationConfirmation(plugin, serviceAccessor, fallToCreate);

        confirmationService.addConfirmation(player, fallCreationConfirmation);
        messageService.sendPlayerMessage(player, Message.FALL_CREATION_POSITION_SET, "/fg confirm");
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 2) {
            return schematicsService.getAvailableSchematicsNames();
        } else if (args.length == 3 || args.length == 4) {
            return IntStream.range(1, 6)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.toUnmodifiableList());
        }
        return List.of();
    }
}
