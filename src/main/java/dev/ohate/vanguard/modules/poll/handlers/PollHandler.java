package dev.ohate.vanguard.modules.poll.handlers;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import dev.ohate.vanguard.Vanguard;
import dev.ohate.vanguard.framework.Handler;
import dev.ohate.vanguard.framework.Module;
import dev.ohate.vanguard.modules.poll.PollModule;
import dev.ohate.vanguard.modules.poll.models.Poll;
import dev.ohate.vanguard.modules.poll.util.PollUtil;
import dev.ohate.vanguard.util.Executor;
import dev.ohate.vanguard.util.JsonUtil;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PollHandler extends Handler {

    private final Set<Poll> dirtyPolls = ConcurrentHashMap.newKeySet();
    private final Set<String> pollUpdateQueue = ConcurrentHashMap.newKeySet();
    private final Map<String, Poll> pollCache = new ConcurrentHashMap<>();

    @Getter
    private static PollHandler instance;

    public PollHandler() {
        instance = this;

        List<Long> guildIds = new ArrayList<>();

        for (Guild guild : Vanguard.getInstance().getJDA().getGuilds()) {
            guildIds.add(guild.getIdLong());
        }

        int pollsLoaded = 0;

        for (Document document : Poll.getCollection().find(Filters.in("guildId", guildIds))) {
            pollsLoaded++;
            addPoll(JsonUtil.readFromJson(document, Poll.class));
        }

        getModule().getLogger().info("Loaded " + pollsLoaded + " polls from " + guildIds.size() + " guilds.");
    }

    public void markPollAsDirty(Poll poll) {
        dirtyPolls.add(poll);
    }

    public void saveDirtyPolls() {
        saveAll(dirtyPolls);
        dirtyPolls.clear();
    }

    public void saveAll() {
        saveAll(pollCache.values());
    }

    public void saveAll(Collection<Poll> polls) {
        if (polls.isEmpty()) {
            return;
        }

        List<WriteModel<Document>> models = new ArrayList<>();

        for (Poll poll : polls) {
            models.add(new ReplaceOneModel<>(
                    Filters.eq(poll.getId()),
                    Document.parse(JsonUtil.writeToJson(poll))
            ));
        }

        Poll.getCollection().bulkWrite(models);
    }

    public void schedulePollUpdate(Poll poll, Message message) {
        dirtyPolls.add(poll);

        if (pollUpdateQueue.contains(message.getId())) {
            return;
        }

        pollUpdateQueue.add(message.getId());

        Executor.schedule(() -> {
            pollUpdateQueue.remove(message.getId());
            message.editMessageEmbeds(PollUtil.buildPollEmbed(poll)).queue(s -> {
            }, e -> removePoll(poll.getId()));
        }, 5, TimeUnit.SECONDS);
    }

    public void addPoll(Poll poll) {
        pollCache.put(poll.getId(), poll);

        Executor.schedule(
                () -> removePoll(poll.getId()),
                Math.max(1L, poll.getEndsAt() - System.currentTimeMillis()),
                TimeUnit.MILLISECONDS
        );
    }

    public void removePoll(String pollId) {
        pollUpdateQueue.remove(pollId);
        Poll.getCollection().deleteOne(Filters.eq(pollId));

        Poll poll = pollCache.remove(pollId);
        if (poll == null) return;

        dirtyPolls.remove(poll);

        Guild guild = Vanguard.getInstance().getJDA().getGuildById(poll.getGuildId());
        if (guild == null) return;

        TextChannel channel = guild.getTextChannelById(poll.getChannelId());
        if (channel == null) return;

        channel.retrieveMessageById(poll.getMessageId())
                .queue(message -> message.editMessageEmbeds(PollUtil.buildPollEmbed(poll))
                        .setComponents(message.getComponents().stream().map(LayoutComponent::asDisabled).toList())
                        .queue());
    }

    public Poll getPoll(String pollId) {
        return pollCache.get(pollId);
    }

    @Override
    public Module getModule() {
        return PollModule.getInstance();
    }

    @Override
    public void saveData() {
        saveDirtyPolls();
    }

}
