package io.github.hugo1307.fallgates;

import com.google.inject.Inject;
import com.google.inject.Injector;
import dev.hugog.minecraft.dev_command.DevCommand;
import dev.hugog.minecraft.dev_command.commands.executors.DevCommandExecutor;
import dev.hugog.minecraft.dev_command.commands.handler.CommandHandler;
import dev.hugog.minecraft.dev_command.dependencies.DependencyHandler;
import dev.hugog.minecraft.dev_command.integration.Integration;
import io.github.hugo1307.fallgates.config.ConfigHandler;
import io.github.hugo1307.fallgates.data.cache.KeyValueCache;
import io.github.hugo1307.fallgates.injection.PluginBinderModule;
import io.github.hugo1307.fallgates.services.GateService;
import io.github.hugo1307.fallgates.services.SchematicsService;
import io.github.hugo1307.fallgates.utils.PluginValidationConfiguration;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class FallGates extends JavaPlugin {

    @Inject
    private ConfigHandler configHandler;

    @Inject
    private GateService gateService;
    @Inject
    private SchematicsService schematicsService;

    @Inject
    private KeyValueCache keyValueCache;

    @Override
    public void onEnable() {
        initDependencyInjection();
        initDevCommands();
        registerCommandsDependencies();
        configHandler.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initDependencyInjection() {
        PluginBinderModule guiceBinderModule = new PluginBinderModule(this);
        Injector injector = guiceBinderModule.createInjector();
        injector.injectMembers(this);
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
        commandHandler.useAutoValidationConfiguration(new PluginValidationConfiguration());
    }

    private void registerCommandsDependencies() {
        DevCommand devCommand = DevCommand.getOrCreateInstance();
        DependencyHandler dependencyHandler = devCommand.getDependencyHandler();
        Integration pluginDevCommandsIntegration = Integration.createFromPlugin(this);

        // Bukkit Related Stuff
        dependencyHandler.registerDependency(pluginDevCommandsIntegration, this);

        // Services
        dependencyHandler.registerDependency(pluginDevCommandsIntegration, gateService);
        dependencyHandler.registerDependency(pluginDevCommandsIntegration, schematicsService);
        
        dependencyHandler.registerDependency(pluginDevCommandsIntegration, keyValueCache);
    }

}
