package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.AutoValidation;
import dev.hugog.minecraft.dev_command.annotations.Command;
import dev.hugog.minecraft.dev_command.annotations.Dependencies;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.ConfirmationService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@AutoValidation
@Command(alias = "confirm", description = "Confirms a operation in the plugin.", permission = "fallgates.command.confirm", isPlayerOnly = true)
@Dependencies(dependencies = {ServiceAccessor.class})
@SuppressWarnings("unused")
public class ConfirmationCommand extends BukkitDevCommand {

    private final ConfirmationService confirmationService;
    private final MessageService messageService;

    public ConfirmationCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);

        ServiceAccessor serviceAccessor = getDependency(ServiceAccessor.class);
        this.confirmationService = serviceAccessor.accessService(ConfirmationService.class);
        this.messageService = serviceAccessor.accessService(MessageService.class);
    }

    @Override
    public void execute() {
        Player player = (Player) getCommandSender();

        if (!confirmationService.hasConfirmation(player)) {
            messageService.sendMessage(player, Message.CONFIRM_NO_OPERATION_PENDING);
            return;
        }
        
        // Execute the confirmation action if it exists
        confirmationService.getConfirmation(player)
                .ifPresent(pluginConfirmation -> pluginConfirmation.onConfirm(player));
    }

    @Override
    public List<String> onTabComplete(String[] strings) {
        return List.of();
    }


}
