package dev.ohate.vanguard.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Executor {

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static void schedule(Runnable command, long delay, TimeUnit unit) {
        EXECUTOR.schedule(command, delay, unit);
    }

}
