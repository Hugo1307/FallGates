package io.github.hugo1307.fallgates.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.config.ConfigHandler;
import io.github.hugo1307.fallgates.data.HibernateHandler;
import io.github.hugo1307.fallgates.data.databases.DatabaseConfiguration;
import io.github.hugo1307.fallgates.data.databases.SqlLiteConfiguration;

import java.util.logging.Logger;

public class PluginBinderModule extends AbstractModule {

    private final FallGates plugin;

    public PluginBinderModule(FallGates plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(FallGates.class).toInstance(plugin);
        this.bind(Logger.class).annotatedWith(Names.named("pluginLogger")).toInstance(plugin.getLogger());

        ConfigHandler configHandler = new ConfigHandler(plugin);
        this.bind(ConfigHandler.class).toInstance(configHandler);

        this.bind(HibernateHandler.class);
        this.bind(DatabaseConfiguration.class).toInstance(new SqlLiteConfiguration());
    }

}
