package io.github.hugo1307.fallgates.config.entities;

/**
 * Represents a configuration entry in the plugin's configuration.
 */
public interface ConfigEntry {

    /**
     * Get the key for the configuration entity.
     *
     * @return the key for the configuration entity
     */
    String getKey();

    /**
     * Get the prefix for the configuration entity.
     *
     * @return the prefix for the configuration entity
     */
    String getConfigPrefix();

}
