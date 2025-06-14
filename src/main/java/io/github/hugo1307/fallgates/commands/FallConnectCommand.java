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


@AutoValidation
@Command(alias = "connect", description = "Connect a fall to another one.", permission = "fallgates.command.connect")
@Arguments({
        @Argument(name = "sourceFall", description = "The id of the source fall.", position = 0, parser = StringArgumentParser.class),
        @Argument(name = "targetFall", description = "The id of the target fall.", position = 1, parser = StringArgumentParser.class)
})
@Dependencies(dependencies = {FallGates.class, ServiceAccessor.class})
public class FallConnectCommand extends BukkitDevCommand {

    private final FallService fallService;
    private final MessageService messageService;

    public FallConnectCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);
        ServiceAccessor serviceAccessor = getDependency(ServiceAccessor.class);
        this.fallService = serviceAccessor.accessService(FallService.class);
        this.messageService = serviceAccessor.accessService(MessageService.class);
    }

    @Override
    public void execute() {
        String sourceFallId = (String) getArgumentParser(0).parse().orElseThrow();
        String targetFallId = (String) getArgumentParser(1).parse().orElseThrow();

        if (!fallService.exists(sourceFallId)) {
            messageService.sendMessage(getCommandSender(), Message.FALL_NOT_FOUND, sourceFallId);
            return;
        }

        if (!fallService.exists(targetFallId)) {
            messageService.sendMessage(getCommandSender(), Message.FALL_NOT_FOUND, targetFallId);
            return;
        }

        Fall sourceFall = fallService.getFallById(sourceFallId).orElseThrow();
        Fall targetFall = fallService.getFallById(targetFallId).orElseThrow();

        if (sourceFall.getTargetFallId() != null || targetFall.getTargetFallId() != null) {
            messageService.sendMessage(getCommandSender(), Message.FALL_CONNECT_ALREADY_CONNECTED);
            return;
        }

        fallService.connectFalls(sourceFall, targetFall);
        messageService.sendMessage(getCommandSender(), Message.FALL_CONNECT_SUCCESS, sourceFall.getName(), targetFall.getName());
    }

    @Override
    public List<String> onTabComplete(String[] strings) {
        return List.of();
    }
}
