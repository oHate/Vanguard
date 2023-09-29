package dev.ohate.vanguard.store.cache;

import com.mongodb.client.model.Filters;
import dev.ohate.vanguard.Vanguard;
import dev.ohate.vanguard.module.poll.models.Poll;
import dev.ohate.vanguard.module.poll.util.PollUtil;
import dev.ohate.vanguard.util.JsonUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollCache {

    private static final ScheduledExecutorService POLL_REMOVAL_EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService POLL_UPDATE_EXECUTOR = Executors.newScheduledThreadPool(1);

    // Message ID -> Poll
    private static final Set<String> BEING_UPDATED = ConcurrentHashMap.newKeySet();
    private static final Map<String, Poll> POLL_CACHE = new ConcurrentHashMap<>();

    public static void loadPolls(JDA jda) {
        List<String> guildIds = new ArrayList<>();

        for (Guild guild : jda.getGuilds()) {
            guildIds.add(guild.getId());
        }

        for (Document document : Poll.getCollection().find(Filters.in("guildId", guildIds))) {
            Poll poll = JsonUtil.readFromJson(document, Poll.class);

            addPoll(poll);
        }
    }

    public static void schedulePollUpdate(Poll poll, Message message) {
        if (BEING_UPDATED.contains(message.getId())) {
            return;
        }

        BEING_UPDATED.add(message.getId());
        POLL_UPDATE_EXECUTOR.schedule(
                () -> {
                    BEING_UPDATED.remove(message.getId());
                    message.editMessageEmbeds(PollUtil.buildPollEmbed(poll)).queue(s -> {}, e -> removePoll(poll.getId()));
                },
                5,
                TimeUnit.SECONDS
        );
    }

    public static void addPoll(Poll poll) {
        POLL_CACHE.put(poll.getId(), poll);
        POLL_REMOVAL_EXECUTOR.schedule(
                () -> removePoll(poll.getId()),
                Math.max(1L, poll.getEndsAt() - System.currentTimeMillis()),
                TimeUnit.MILLISECONDS
        );
    }

    public static void removePoll(String pollId) {
        POLL_CACHE.remove(pollId);
        Poll.getCollection().deleteOne(Filters.eq(pollId));
    }

    public static Poll getPoll(String pollId) {
        return POLL_CACHE.get(pollId);
    }

}
