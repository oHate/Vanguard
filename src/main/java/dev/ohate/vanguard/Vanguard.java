package dev.ohate.vanguard;

import com.mongodb.client.MongoDatabase;
import dev.ohate.vanguard.module.poll.PollModule;
import dev.ohate.vanguard.util.Command;
import dev.ohate.vanguard.framework.Framework;
import dev.ohate.vanguard.framework.Module;
import dev.ohate.vanguard.module.poll.commands.PollCommand;
import dev.ohate.vanguard.store.cache.PollCache;
import dev.ohate.vanguard.store.mongo.Mongo;
import dev.ohate.vanguard.util.VanguardConfig;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Getter
public class Vanguard extends Framework {

    @Getter
    private static Vanguard instance;

    private final List<Module> enabledModules = new ArrayList<>();

    private final JDA jda;
    private final VanguardConfig config;
    private final Mongo mongo;
    private final MongoDatabase database;

    public Vanguard() throws InterruptedException {
        instance = this;

        config = new VanguardConfig();

        mongo = new Mongo();
        mongo.connect(config.getMongoUri());

        database = mongo.getClient().getDatabase("vanguard");

        jda = JDABuilder.createDefault("").build();
        jda.awaitReady();

        enabledModules.addAll(List.of(
                new PollModule()
        ));

        PollCache.loadPolls(jda);
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
