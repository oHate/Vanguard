package dev.ohate.vanguard.module.poll;

import dev.ohate.vanguard.Vanguard;
import dev.ohate.vanguard.framework.Framework;
import dev.ohate.vanguard.framework.Handler;
import dev.ohate.vanguard.framework.Module;
import dev.ohate.vanguard.module.poll.commands.PollCommand;
import dev.ohate.vanguard.module.poll.handlers.PollBuilderHandler;
import dev.ohate.vanguard.module.poll.handlers.PollHandler;
import dev.ohate.vanguard.module.poll.listeners.PollListener;
import dev.ohate.vanguard.util.Command;
import lombok.Getter;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class PollModule extends Module {

    @Getter
    private static PollModule instance;

    public PollModule() {
        instance = this;
    }

    @Override
    public Framework getFramework() {
        return Vanguard.getInstance();
    }

    @Override
    public String getName() {
        return "Poll";
    }

    @Override
    public String getConfigFileName() {
        return "poll";
    }

    @Override
    public List<Handler> getHandlers() {
        return List.of(new PollBuilderHandler(), new PollHandler());
    }

    @Override
    public List<ListenerAdapter> getListeners() {
        return List.of(new PollListener());
    }

    @Override
    public List<Command> getCommands() {
        return List.of(new PollCommand());
    }

}
