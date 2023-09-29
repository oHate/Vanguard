package dev.ohate.vanguard.module.poll.handlers;

import dev.ohate.vanguard.framework.Handler;
import dev.ohate.vanguard.framework.Module;
import dev.ohate.vanguard.module.poll.PollModule;
import lombok.Getter;

public class PollHandler extends Handler {

    @Getter
    private static PollHandler instance;

    public PollHandler() {
        instance = this;
    }

    @Override
    public Module getModule() {
        return PollModule.getInstance();
    }

}
