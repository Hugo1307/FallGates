package io.github.hugo1307.fallgates.utils;

import com.google.inject.Inject;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.validation.IAutoValidationConfiguration;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;

public class PluginValidationConfiguration implements IAutoValidationConfiguration {

    private final MessageService messageService;

    @Inject
    public PluginValidationConfiguration(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public String getNoPermissionMessage(BukkitDevCommand bukkitDevCommand) {
        return messageService.getMessage(Message.NO_PERMISSION);
    }

    @Override
    public String getInvalidArgumentsMessage(BukkitDevCommand bukkitDevCommand) {
        return messageService.getMessage(Message.INVALID_ARGUMENTS);
    }

    @Override
    public String getInvalidSenderMessage(BukkitDevCommand bukkitDevCommand) {
        return messageService.getMessage(Message.INVALID_SENDER);
    }

}
