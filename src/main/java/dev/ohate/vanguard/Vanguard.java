package dev.ohate.vanguard;

import com.google.gson.JsonSyntaxException;
import com.mongodb.client.MongoDatabase;
import dev.ohate.vanguard.framework.Framework;
import dev.ohate.vanguard.framework.Module;
import dev.ohate.vanguard.modules.poll.PollModule;
import dev.ohate.vanguard.mongo.Mongo;
import dev.ohate.vanguard.util.JsonUtil;
import dev.ohate.vanguard.util.VanguardConfig;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Vanguard extends Framework {

    @Getter
    private static Vanguard instance;

    private VanguardConfig config;
    private JDA jda;
    private Mongo mongo;
    private MongoDatabase database;

    private final List<Module> enabledModules = new ArrayList<>();

    public Vanguard() throws InterruptedException {
        instance = this;

        try {
            loadConfig(new File("config", "vanguard.json"));
        } catch (IOException e) {
            getLogger().error("An error has occurred while loading the config:", e);
            shutdown();
            return;
        }

        if (config.getToken().isEmpty()) {
            getLogger().error("The discord bots token is not present in the config file.");
            shutdown();
            return;
        }

        mongo = new Mongo();
        mongo.connect(config.getMongoUri());

        database = mongo.getClient().getDatabase("vanguard");

        jda = JDABuilder.createDefault(config.getToken()).build();
        jda.awaitReady();

        enabledModules.addAll(List.of(
                new PollModule()
        ));
    }

    private void loadConfig(File configFile) throws IOException, JsonSyntaxException {
        if (!configFile.exists()) {
            config = new VanguardConfig();

            configFile.getParentFile().mkdirs();
            Files.writeString(configFile.toPath(), JsonUtil.writeToPrettyJson(config));
        } else {
            config = JsonUtil.readFromJson(new String(Files.readAllBytes(configFile.toPath())), VanguardConfig.class);
        }
    }

    @Override
    public String getName() {
        return "Vanguard";
    }

    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public List<Module> getModules() {
        return enabledModules;
    }

    @Override
    public File getDataFolder() {
        return new File("data");
    }

}
