package io.github.hugo1307.fallgates.data.databases;

import io.github.hugo1307.fallgates.config.ConfigHandler;
import io.github.hugo1307.fallgates.config.entities.DatabaseConfigEntry;
import io.github.hugo1307.fallgates.exceptions.DbmsException;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.Properties;

public class SqlLiteConfiguration implements DatabaseConfiguration {

    @Override
    public Configuration getConfiguration(ConfigHandler configHandler) {

        Configuration configuration = new Configuration();
        Properties dbConnectionProperties = new Properties();

        String dbName = configHandler.getValue(DatabaseConfigEntry.DATABASE_NAME, String.class);
        String dbUser = configHandler.getValue(DatabaseConfigEntry.DATABASE_USER, String.class);
        String dbPassword = configHandler.getValue(DatabaseConfigEntry.DATABASE_PASSWORD, String.class);
        String databasePath = getDatabasePath(configHandler.getDataFolder(), dbName);

        dbConnectionProperties.put("hibernate.connection.url", String.format("jdbc:sqlite:%s", databasePath));
        dbConnectionProperties.put("hibernate.connection.username", dbUser);
        dbConnectionProperties.put("hibernate.connection.password", dbPassword);
        dbConnectionProperties.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        dbConnectionProperties.put("hibernate.connection.driver_class", "org.sqlite.JDBC");
        dbConnectionProperties.putAll(getCommonProperties());

        configuration.setProperties(dbConnectionProperties);

        return configuration;

    }

    private String getDatabasePath(File pluginDataFolder, String dbName) {
        File parentDirectory = new File(pluginDataFolder, "database");
        if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new DbmsException("Could not create database directory");
        }
        return new File(parentDirectory, dbName).getAbsolutePath();
    }

}
