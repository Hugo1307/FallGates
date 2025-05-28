package io.github.hugo1307.fallgates.data.databases;

import io.github.hugo1307.fallgates.config.ConfigHandler;
import org.hibernate.cfg.Configuration;

import java.util.Map;

/**
 * Represents a database configuration.
 *
 * <p>
 * It allows the abstraction of the database, allowing the usage of different databases without affecting the rest of
 * the code.
 */
public interface DatabaseConfiguration {
    /**
     * Get a Hibernate Configuration object with the database configuration
     *
     * @param bukkitConfigHandler the Bukkit configuration handler to access the plugin's configuration
     * @return the Hibernate Configuration object
     */
    Configuration getConfiguration(ConfigHandler bukkitConfigHandler);

    /**
     * Get the common properties for the database configuration
     *
     * @return the common properties for all dbms
     */
    default Map<?, ?> getCommonProperties() {
        return Map.of(
                "hibernate.hbm2ddl.auto", "update",
                "hibernate.connection.provider_class", "com.zaxxer.hikari.hibernate.HikariConnectionProvider",
                "hibernate.hikari.maximumPoolSize", "5",
                "hibernate.hikari.minimumIdle", "1",
                "hibernate.jdbc.batch_size", "50",
                "hibernate.show_sql", false,
                "hibernate.generate_statistics", false,
                "hibernate.order_inserts", true,
                "hibernate.order_updates", true,
                "hibernate.batch_versioned_data", true
        );
    }

}
