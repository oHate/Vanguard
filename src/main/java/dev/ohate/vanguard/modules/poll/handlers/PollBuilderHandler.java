package dev.ohate.vanguard.modules.poll.handlers;

import dev.ohate.vanguard.framework.Handler;
import dev.ohate.vanguard.framework.Module;
import dev.ohate.vanguard.modules.poll.PollModule;
import dev.ohate.vanguard.modules.poll.models.Poll;
import dev.ohate.vanguard.util.Executor;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PollBuilderHandler extends Handler {

    private final Map<UUID, Poll.Builder> activeBuilderMap;

    @Getter
    private static PollBuilderHandler instance;

    public PollBuilderHandler() {
        instance = this;
        activeBuilderMap = new ConcurrentHashMap<>();
    }

    public void addPollBuilder(UUID id, Poll.Builder builder) {
        activeBuilderMap.put(id, builder);
        Executor.schedule(() -> removePollBuilder(id), Poll.Builder.TTL, TimeUnit.MILLISECONDS);
    }

    public void removePollBuilder(UUID id) {
        activeBuilderMap.remove(id);
    }

    public Poll.Builder getPollBuilder(UUID id) {
        return activeBuilderMap.get(id);
    }

    @Override
    public Module getModule() {
        return PollModule.getInstance();
    }

}
