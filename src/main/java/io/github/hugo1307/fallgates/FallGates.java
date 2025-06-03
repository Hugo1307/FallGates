package io.github.hugo1307.fallgates;

import com.google.inject.Inject;
import com.google.inject.Injector;
import dev.hugog.minecraft.dev_command.DevCommand;
import dev.hugog.minecraft.dev_command.commands.executors.DevCommandExecutor;
import dev.hugog.minecraft.dev_command.commands.handler.CommandHandler;
import dev.hugog.minecraft.dev_command.dependencies.DependencyHandler;
import dev.hugog.minecraft.dev_command.integration.Integration;
import io.github.hugo1307.fallgates.config.ConfigHandler;
import io.github.hugo1307.fallgates.injection.PluginBinderModule;
import io.github.hugo1307.fallgates.listeners.FallInteractListener;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.FallService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import io.github.hugo1307.fallgates.utils.PluginValidationConfiguration;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class FallGates extends JavaPlugin {

    @Getter
    private Injector guiceInjector;

    @Inject
    private ConfigHandler configHandler;
    private ServiceAccessor serviceAccessor;
    
    @Inject
    private FallInteractListener fallInteractListener;

    @Override
    public void onEnable() {
        initDependencyInjection();

        // Initialize the ServiceAccessor after Guice injector is set up
        this.serviceAccessor = new ServiceAccessor(this);

        initDevCommands();
        registerCommandsDependencies();

        // Initialize the configuration handler
        configHandler.init();

        registerListeners();
        loadData();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initDependencyInjection() {
        PluginBinderModule guiceBinderModule = new PluginBinderModule(this);
        this.guiceInjector = guiceBinderModule.createInjector();
        this.guiceInjector.injectMembers(this);
    }

    private void initDevCommands() {
        PluginCommand mainPluginCommand = getCommand("fg");
        Integration pluginDevCommandsIntegration = Integration.createFromPlugin(this);

        if (mainPluginCommand == null) {
            return;
        }

        DevCommandExecutor commandExecutor = new DevCommandExecutor("fg", pluginDevCommandsIntegration);
        mainPluginCommand.setExecutor(commandExecutor);
        mainPluginCommand.setTabCompleter(commandExecutor);

        DevCommand devCommand = DevCommand.getOrCreateInstance();
        CommandHandler commandHandler = devCommand.getCommandHandler();

        commandHandler.initCommandsAutoConfiguration(pluginDevCommandsIntegration);
        commandHandler.useAutoValidationConfiguration(new PluginValidationConfiguration(serviceAccessor.accessService(MessageService.class)));
    }

    private void registerCommandsDependencies() {
        DevCommand devCommand = DevCommand.getOrCreateInstance();
        DependencyHandler dependencyHandler = devCommand.getDependencyHandler();
        Integration pluginDevCommandsIntegration = Integration.createFromPlugin(this);

        dependencyHandler.registerDependency(pluginDevCommandsIntegration, this);
        dependencyHandler.registerDependency(pluginDevCommandsIntegration, serviceAccessor);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(fallInteractListener, this);
    }

    private void loadData() {
        // Load all falls from the database to the cache
        serviceAccessor.accessService(FallService.class).loadFalls().thenRun(() -> {
            getLogger().info("All falls loaded from the database.");
        }).exceptionally(ex -> {
            getLogger().severe("Failed to load falls from the database: " + ex.getMessage());
            return null;
        });
    }

}
