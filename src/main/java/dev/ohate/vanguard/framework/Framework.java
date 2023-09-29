package dev.ohate.vanguard.framework;

import dev.ohate.vanguard.util.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Framework {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final Logger logger = LoggerFactory.getLogger(getName());
    private final List<Module> enabledModules = new ArrayList<>();

    public abstract String getName();
    public abstract JDA getJDA();
    public abstract List<Module> getModules();
    public abstract File getDataFolder();

    public void onEnable() {
        try {
            loadModules();
        } catch (Exception e) {
            logger.error("Failed to load modules:", e);
            shutdown();
            return;
        }

        executor.scheduleAtFixedRate(this::saveModules, 3, 3, TimeUnit.MINUTES);
    }

    public void onDisable() {
        disableModules();
    }

    public void shutdown() {
        System.exit(1);
    }

    public void loadModules() {
        List<Command> commands = new ArrayList<>();

        for (Module module : getModules()) {
            long start = System.currentTimeMillis();
            logger.info("Loading " + module.getName() + " module...");

            module.setLoaded(true);
            module.onEnable();

            for (ListenerAdapter listener : module.getListeners()) {
                getJDA().addEventListener(listener);
            }

            commands.addAll(module.getCommands());

            enabledModules.add(module);
            logger.info("Enabled " + module.getName() + " Module! " + (System.currentTimeMillis() - start) + "ms to load.");
        }

        List<SlashCommandData> commandData = new ArrayList<>();

        for (Command command : commands) {
            getJDA().addEventListener(command);
            commandData.add(command.getCommand());
        }

        logger.info("Registering " + commandData.size() + " commands!");

        // TODO -> Check if commands have already been registered?
//        getJDA().updateCommands().addCommands(commandData).complete();
    }

    public void disableModules() {
        for (Module module : enabledModules) {
            logger.info("Disabling " + module.getName() + " module...");

            try {
                module.onDisable();
                module.setLoaded(false);

                logger.info("Disabled " + module.getName() + " module!");
            } catch (Exception e) {
                logger.error("Failed to disable " + module.getName() + " module:", e);
            }
        }
    }

    public void saveModules() {
        getModules().forEach(module -> {
            try {
                module.onAutoSave();
            } catch (Exception e) {
                logger.error("Failed to save " + module.getName() + " module:", e);
            }
        });
    }

}
