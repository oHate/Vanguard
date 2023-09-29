package dev.ohate.vanguard.store.cache;

import dev.ohate.vanguard.module.poll.models.Poll;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class PollBuilderCache {

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final Map<UUID, Poll.Builder> POLL_CREATION = new ConcurrentHashMap<>();

    public static void addPollBuilder(UUID id, Poll.Builder builder) {
        POLL_CREATION.put(id, builder);
        EXECUTOR.schedule(() -> removePollBuilder(id), Poll.Builder.TTL, TimeUnit.MILLISECONDS);
    }

    public static void removePollBuilder(UUID id) {
        POLL_CREATION.remove(id);
    }

    public static Poll.Builder getPollBuilder(UUID id) {
        return POLL_CREATION.get(id);
    }

}
