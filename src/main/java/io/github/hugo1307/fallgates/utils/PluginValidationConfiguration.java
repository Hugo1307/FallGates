package io.github.hugo1307.fallgates.utils;

import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.validation.IAutoValidationConfiguration;
import org.bukkit.ChatColor;

public class PluginValidationConfiguration implements IAutoValidationConfiguration {

    @Override
    public String getNoPermissionMessage(BukkitDevCommand bukkitDevCommand) {
        return ChatColor.RED + "You do not have permission to execute this command.";
    }

    @Override
    public String getInvalidArgumentsMessage(BukkitDevCommand bukkitDevCommand) {
        return ChatColor.RED + "Invalid arguments provided for this command.";
    }

    @Override
    public String getInvalidSenderMessage(BukkitDevCommand bukkitDevCommand) {
        return ChatColor.RED + "This command can only be executed by a player.";
    }

}
