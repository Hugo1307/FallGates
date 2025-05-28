package io.github.hugo1307.fallgates.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.hugo1307.fallgates.FallGates;

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
    }

}
