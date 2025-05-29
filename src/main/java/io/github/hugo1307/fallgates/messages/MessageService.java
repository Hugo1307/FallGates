package io.github.hugo1307.fallgates.messages;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.services.Service;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.MessageFormat;

/**
 * Service for handling messages in the FallGates plugin.
 */
@Singleton
public final class MessageService implements Service {

    private static final String MESSAGES_FILE_NAME = "messages.yml";

    private final FallGates plugin;
    private final YamlConfiguration messagesConfig;

    @Inject
    public MessageService(FallGates plugin) {
        this.plugin = plugin;
        this.messagesConfig = getMessagesConfig();
    }

    public String getPrefix() {
        return ChatColor.GREEN + "FallGates" + ChatColor.GRAY + " > ";
    }

    public String getMessage(Message message, String... arguments) {
        return MessageFormat.format(getPrefix() + this.messagesConfig.getString(message.getKey(), "N/A"), arguments);
    }

    public void sendPlayerMessage(Player player, Message message, String... arguments) {
        player.sendMessage(getMessage(message, arguments));
    }

    public YamlConfiguration getMessagesConfig() {
        if (!getMessagesFile().exists()) {
            plugin.getLogger().warning("Messages file not found. Creating a default one.");
            plugin.saveResource(MESSAGES_FILE_NAME, false);
        }
        return YamlConfiguration.loadConfiguration(getMessagesFile());
    }

    private File getMessagesFile() {
        return new File(plugin.getDataFolder(), MESSAGES_FILE_NAME);
    }

}
