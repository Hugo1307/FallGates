package io.github.hugo1307.fallgates.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.config.entities.ConfigEntry;

import java.io.File;

@Singleton
public class ConfigHandler {

    private final FallGates plugin;

    @Inject
    public ConfigHandler(FallGates plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the configuration entry value from the plugin's config file.
     *
     * @param entity the configuration entry to retrieve
     * @param type   the type of the value to retrieve
     * @param <T>    the type of the value to retrieve
     * @return the value of the configuration entry, or null if not found
     */
    public <T> T getValue(ConfigEntry entity, Class<T> type) {
        return plugin.getConfig().getObject(entity.getConfigPrefix() + "." + entity.getKey(), type);
    }

    /**
     * Initializes the configuration file.
     *
     * <p>
     * This method will save the default configuration file if it does not exist.
     */
    public void init() {
        if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
    }

    /**
     * Get the plugin's data folder.
     *
     * @return the plugin's data folder
     */
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

}
