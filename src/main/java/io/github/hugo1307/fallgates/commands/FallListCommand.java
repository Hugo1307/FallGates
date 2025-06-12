package io.github.hugo1307.fallgates.commands;

import dev.hugog.minecraft.dev_command.annotations.AutoValidation;
import dev.hugog.minecraft.dev_command.annotations.Command;
import dev.hugog.minecraft.dev_command.annotations.Dependencies;
import dev.hugog.minecraft.dev_command.commands.BukkitDevCommand;
import dev.hugog.minecraft.dev_command.commands.data.BukkitCommandData;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.FallService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AutoValidation
@Command(alias = "list", description = "List all existing falls.", permission = "fallgates.command.list")
@Dependencies(dependencies = {ServiceAccessor.class})
public class FallListCommand extends BukkitDevCommand {

    private final FallService fallService;
    private final MessageService messageService;

    public FallListCommand(BukkitCommandData commandData, CommandSender commandSender, String[] args) {
        super(commandData, commandSender, args);
        ServiceAccessor serviceAccessor = getDependency(ServiceAccessor.class);
        this.fallService = serviceAccessor.accessService(FallService.class);
        this.messageService = serviceAccessor.accessService(MessageService.class);
    }

    @Override
    public void execute() {
        List<Fall> allFalls = fallService.getAllFalls().stream()
                .sorted(Comparator.comparingLong(Fall::getId))
                .collect(Collectors.toList());

        getCommandSender().sendMessage(messageService.getMessageWithoutPrefix(Message.HEADER, ChatColor.GREEN + "FallGates"));
        if (!fallService.getAllFalls().isEmpty()) {
            for (Fall fall : allFalls) {
                getCommandSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + fall.getName());
                getCommandSender().sendMessage("");
                getCommandSender().sendMessage(messageService.getMessageWithoutPrefix(Message.FALL_LIST_ID, String.valueOf(fall.getId())));
                getCommandSender().sendMessage(messageService.getMessageWithoutPrefix(Message.FALL_LIST_LOCATION,
                        String.valueOf(Math.round(fall.getPosition().getX())), String.valueOf(Math.round(fall.getPosition().getY())),
                        String.valueOf(Math.round(fall.getPosition().getZ())), fall.getPosition().getWorld()));
                getCommandSender().sendMessage(messageService.getMessageWithoutPrefix(Message.FALL_LIST_CONNECTED,
                        fall.getTargetFallId() != null ? fallService.getFallById(fall.getTargetFallId()).map(Fall::getName).orElse("N/A") : ChatColor.RED + "None"));
                getCommandSender().sendMessage("");
            }
        } else {
            messageService.sendMessage(getCommandSender(), Message.FALL_LIST_NO_FALLS);
        }
        getCommandSender().sendMessage(messageService.getMessageWithoutPrefix(Message.FOOTER));
    }

    @Override
    public List<String> onTabComplete(String[] strings) {
        return List.of();
    }
}
