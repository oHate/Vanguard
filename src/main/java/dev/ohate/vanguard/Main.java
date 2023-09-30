package dev.ohate.vanguard;

import dev.ohate.vanguard.modules.poll.handlers.PollHandler;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        new Vanguard().onEnable();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PollHandler handler = PollHandler.getInstance();

            if (handler != null && handler.isLoaded()) {
                handler.saveDirtyPolls();
            }
        }));
    }

}