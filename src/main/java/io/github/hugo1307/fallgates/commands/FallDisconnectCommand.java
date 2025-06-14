package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.*;
import dev.hugog.minecraft.dev_command.arguments.parsers.StringArgumentParser;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.FallService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

@AutoValidation
@Command(alias = "disconnect", description = "Disconnect a fall from another one.", permission = "fallgates.command.disconnect")
@Arguments({
        @Argument(name = "fall", description = "The id of the fall to disconnect.", position = 0, parser = StringArgumentParser.class),
})
@Dependencies(dependencies = {FallGates.class, ServiceAccessor.class})
public class FallDisconnectCommand extends BukkitDevCommand {

    private final FallService fallService;
    private final MessageService messageService;

    public FallDisconnectCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);
        ServiceAccessor serviceAccessor = getDependency(ServiceAccessor.class);
        this.fallService = serviceAccessor.accessService(FallService.class);
        this.messageService = serviceAccessor.accessService(MessageService.class);
    }

    @Override
    public void execute() {
        String fallId = (String) getArgumentParser(0).parse().orElseThrow();

        if (!fallService.exists(fallId)) {
            messageService.sendMessage(getCommandSender(), Message.FALL_NOT_FOUND, fallId);
            return;
        }

        Fall sourceFall = fallService.getFallById(fallId).orElseThrow();
        if (sourceFall.getTargetFallId() == null) {
            messageService.sendMessage(getCommandSender(), Message.FALL_DISCONNECT_NOT_CONNECTED);
            return;
        }

        fallService.disconnectFall(sourceFall);
        messageService.sendMessage(getCommandSender(), Message.FALL_DISCONNECT_SUCCESS, sourceFall.getName());
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 1) {
            return fallService.getAllFalls().stream()
                    .filter(Fall::isConnected)
                    .map(Fall::getId)
                    .collect(Collectors.toUnmodifiableList());
        }
        return List.of();
    }
}
