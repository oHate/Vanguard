package dev.ohate.vanguard.module.poll.handlers;

import dev.ohate.vanguard.framework.Handler;
import dev.ohate.vanguard.framework.Module;
import dev.ohate.vanguard.module.poll.PollModule;
import lombok.Getter;

public class PollBuilderHandler extends Handler {

    @Getter
    private static PollBuilderHandler instance;

    public PollBuilderHandler() {
        instance = this;
    }

    @Override
    public Module getModule() {
        return PollModule.getInstance();
    }

}
