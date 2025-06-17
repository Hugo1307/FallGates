package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.DevCommand;
import dev.hugog.minecraft.dev_command.annotations.AutoValidation;
import dev.hugog.minecraft.dev_command.annotations.Command;
import dev.hugog.minecraft.dev_command.annotations.Dependencies;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.AbstractCommandData;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import dev.hugog.minecraft.dev_command.integration.Integration;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AutoValidation
@Command(alias = "help", description = "Provides a help page with all commands.", permission = "fallgates.command.help")
@Dependencies(dependencies = {ServiceAccessor.class})
public class HelpCommand extends BukkitDevCommand {

    private final FallGates plugin;
    private final MessageService messageService;

    public HelpCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);
        this.plugin = getDependency(FallGates.class);
        this.messageService = getDependency(ServiceAccessor.class).accessService(MessageService.class);
    }

    @Override
    public void execute() {

        DevCommand devCommand = DevCommand.getOrCreateInstance();
        List<AbstractCommandData> allCommands = devCommand.getCommandHandler().getRegisteredCommands(Integration.createFromPlugin(plugin)).stream()
                .map(BukkitCommandData.class::cast)
                .filter(commandData -> getCommandSender().hasPermission(commandData.getPermission()))
                .sorted(Comparator.comparing(AbstractCommandData::getAlias))
                .collect(Collectors.toUnmodifiableList());

        getCommandSender().sendMessage(messageService.getMessageWithoutPrefix(Message.HEADER, ChatColor.GREEN + "FallGates"));
        getCommandSender().sendMessage("");

        allCommands.forEach(commandData -> {
            String commandText = String.format("/%s %s %s", "fg", commandData.getAlias(), getCommandArgumentsString((BukkitCommandData) commandData));
            TextComponent commandComponent = new TextComponent(String.format("  • /%s %s", "fg", commandData.getAlias()));
            commandComponent.addExtra(getCommandArgumentsComponent((BukkitCommandData) commandData));
            commandComponent.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            commandComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandText));
            commandComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to suggest command")));
            getCommandSender().spigot().sendMessage(commandComponent);
            getCommandSender().sendMessage(ChatColor.GREEN + "      ╰ " + ChatColor.GRAY + commandData.getDescription());
            getCommandSender().sendMessage("");
        });

        getCommandSender().sendMessage(messageService.getMessageWithoutPrefix(Message.FOOTER));
    }

    private String getCommandArgumentsString(BukkitCommandData commandData) {

        StringBuilder commandArgumentsBuilder = new StringBuilder();
        if (commandData.getArguments() == null) {
            return commandArgumentsBuilder.toString();
        }

        Arrays.stream(commandData.getArguments()).forEach(commandArgument -> {
            if (commandArgument.optional()) {
                commandArgumentsBuilder.append(String.format("[%s] ", commandArgument.name()));
            } else {
                commandArgumentsBuilder.append(String.format("<%s> ", commandArgument.name()));
            }
        });
        return commandArgumentsBuilder.toString();

    }

    private BaseComponent getCommandArgumentsComponent(BukkitCommandData commandData) {

        BaseComponent commandArgumentsBuilder = new TextComponent();
        if (commandData.getArguments() == null) {
            return commandArgumentsBuilder;
        }

        Arrays.stream(commandData.getArguments()).forEach(commandArgument -> {
            TextComponent argumentComponent;
            if (commandArgument.optional()) {
                argumentComponent = new TextComponent(" [" + commandArgument.name() + "]");
            } else {
                argumentComponent = new TextComponent(" <" + commandArgument.name() + ">");
            }
            argumentComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(commandArgument.description())));
            commandArgumentsBuilder.addExtra(argumentComponent);
        });
        return commandArgumentsBuilder;

    }

    @Override
    public List<String> onTabComplete(String[] strings) {
        return List.of();
    }
}
