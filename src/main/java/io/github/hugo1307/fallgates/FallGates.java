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
import io.github.hugo1307.fallgates.listeners.FallEnterListener;
import io.github.hugo1307.fallgates.listeners.FallInteractListener;
import io.github.hugo1307.fallgates.listeners.FallLandingListener;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.FallService;
import io.github.hugo1307.fallgates.services.SchematicsService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import io.github.hugo1307.fallgates.utils.PluginValidationConfiguration;
import io.github.hugo1307.fallgates.utils.ResourceUtils;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.logging.Level;

public final class FallGates extends JavaPlugin {

    @Getter
    private Injector guiceInjector;

    @Inject
    private ConfigHandler configHandler;
    private ServiceAccessor serviceAccessor;

    @Inject
    private FallInteractListener fallInteractListener;
    @Inject
    private FallEnterListener fallEnterListener;
    @Inject
    private FallLandingListener fallLandingListener;

    @Override
    public void onEnable() {
        initDependencyInjection();

        // Initialize the ServiceAccessor after Guice injector is set up
        this.serviceAccessor = new ServiceAccessor(this);

        initDevCommands();
        initSchematicsData();
        registerCommandsDependencies();

        // Initialize the configuration handler
        configHandler.init();

        registerListeners();
        registerTasks();
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

    private void initSchematicsData() {
        Path[] pathsToCreate = {
                Path.of(getDataFolder().getPath(), SchematicsService.SCHEMATICS_PATH.toString()),
                Path.of(getDataFolder().getPath(), SchematicsService.TERRAIN_BACKUP_PATH.toString())
        };
        Path[] defaultSchematics = {
                Path.of("schematics/default_fall.schem")
        };

        for (Path path : pathsToCreate) {
            if (path.toFile().exists()) {
                continue;
            }

            if (!path.toFile().mkdirs()) {
                getLogger().log(Level.SEVERE, "Failed to create Schematics directory {0}", path);
                continue;
            }

            getLogger().log(Level.INFO, "Created Schematics directory {0}", path);
            if (path.endsWith(SchematicsService.SCHEMATICS_PATH.toString())) {
                // Copy default schematics to the schematics directory
                for (Path schematic : defaultSchematics) {
                    Path outputPath = path.resolve(schematic.getFileName());
                    ResourceUtils.copyResource(this, schematic.toString(), outputPath.toFile());
                    getLogger().log(Level.INFO, "Copied default schematic {0} to {1}", new Object[]{schematic, outputPath});
                }
            }
        }
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
        getServer().getPluginManager().registerEvents(fallEnterListener, this);
        getServer().getPluginManager().registerEvents(fallLandingListener, this);
    }

    private void registerTasks() {
        // Task to process closing falls every 1 second
        getServer().getScheduler().scheduleSyncRepeatingTask(this,
                () -> serviceAccessor.accessService(FallService.class).processClosingFalls(), 0L, 20L);
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
