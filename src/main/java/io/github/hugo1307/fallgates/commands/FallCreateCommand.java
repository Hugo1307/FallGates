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
import io.github.hugo1307.fallgates.exceptions.SchematicException;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.ConfirmationService;
import io.github.hugo1307.fallgates.services.FallService;
import io.github.hugo1307.fallgates.services.SchematicsService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AutoValidation
@Command(alias = "create", description = "Create a new fall.", permission = "fallgates.command.create", isPlayerOnly = true)
@Arguments({
        @Argument(name = "name", description = "The name of the fall.", position = 0, parser = StringArgumentParser.class),
        @Argument(name = "schematicName", description = "The name of the schematic to use.", position = 1, parser = StringArgumentParser.class, optional = true),
        @Argument(name = "material", description = "The material of the Fall.", position = 2, parser = MaterialArgumentParser.class, optional = true),
        @Argument(name = "xSize", description = "The size of the fall on X axis.", position = 3, parser = IntegerArgumentParser.class, optional = true),
        @Argument(name = "zSize", description = "The size of the fall on Z axis.", position = 4, parser = IntegerArgumentParser.class, optional = true)
})
@Dependencies(dependencies = {FallGates.class, ServiceAccessor.class})
@SuppressWarnings("unused")
public class FallCreateCommand extends BukkitDevCommand {

    private final FallGates plugin;
    private final FallService fallService;
    private final ConfirmationService confirmationService;
    private final MessageService messageService;
    private final SchematicsService schematicsService;

    private static final int DEFAULT_SIZE = 3;

    public FallCreateCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);

        this.plugin = getDependency(FallGates.class);
        ServiceAccessor serviceAccessor = getDependency(ServiceAccessor.class);
        this.fallService = serviceAccessor.accessService(FallService.class);
        this.confirmationService = serviceAccessor.accessService(ConfirmationService.class);
        this.messageService = serviceAccessor.accessService(MessageService.class);
        this.schematicsService = serviceAccessor.accessService(SchematicsService.class);
    }

    @Override
    public void execute() {
        Player player = (Player) getCommandSender();

        String fallName = ((StringArgumentParser) getArgumentParser(0)).parse().orElseThrow();
        String schematicName = getOptionalArgumentParser(1)
                .map(parser -> ((StringArgumentParser) parser).parse().orElse("default_fall"))
                .orElse("default_fall");
        Material material = getOptionalArgumentParser(2)
                .map(parser -> ((MaterialArgumentParser) parser).parse().orElse(Material.SPRUCE_PLANKS))
                .orElse(Material.SPRUCE_PLANKS);
        int xSize = getOptionalArgumentParser(3)
                .map(parser -> ((IntegerArgumentParser) parser).parse().orElse(DEFAULT_SIZE))
                .orElse(DEFAULT_SIZE);
        int zSize = getOptionalArgumentParser(4)
                .map(parser -> ((IntegerArgumentParser) parser).parse().orElse(DEFAULT_SIZE))
                .orElse(DEFAULT_SIZE);

        if (fallService.existsByName(fallName)) {
            messageService.sendMessage(player, Message.FALL_CREATION_ALREADY_EXISTS_NAME, fallName);
            return;
        }

        Location schematicLocation = player.getLocation().clone().add(0, 1, 0);
        if (!schematicsService.isSchematicAvailable(schematicName)) {
            messageService.sendMessage(player, Message.FALL_CREATION_INVALID_SCHEMATIC, schematicName);
            return;
        }

        FallGateSchematic schematic;
        try {
            schematic = schematicsService.loadSchematic(schematicsService.getSchematicPath(schematicName));
        } catch (SchematicException e) {
            messageService.sendMessage(player, Message.FALL_CREATION_ERROR_LOADING_SCHEMATIC, schematicName);
            plugin.getLogger().severe("Failed to load schematic: Caused by: " + e.getCause());
            e.printStackTrace();
            return;
        }

        Fall fallToCreate = new Fall(null, fallName, Position.fromBukkitLocation(schematicLocation), material, xSize, zSize);
        fallToCreate.setSchematic(schematic);

        FallCreationConfirmation fallCreationConfirmation = new FallCreationConfirmation(plugin, getDependency(ServiceAccessor.class), fallToCreate);
        confirmationService.addConfirmation(player, fallCreationConfirmation);
        messageService.sendMessage(player, Message.FALL_CREATION_POSITION_SET, "/fg confirm");
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 2) {
            return schematicsService.getAvailableSchematicsNames();
        } else if (args.length == 3) {
            String materialInput = args[2].toUpperCase();
            return Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .filter(Material::isSolid)
                    .map(Material::name)
                    .filter(name -> name.startsWith(materialInput))
                    .collect(Collectors.toUnmodifiableList());
        } else if (args.length == 4 || args.length == 5) {
            return IntStream.range(1, 6)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.toUnmodifiableList());
        }
        return List.of();
    }
}
