package dev.ohate.vanguard.modules.poll.models;

import com.google.gson.annotations.SerializedName;
import com.mongodb.client.MongoCollection;
import dev.ohate.vanguard.Vanguard;
import dev.ohate.vanguard.util.EmbedUtil;
import dev.ohate.vanguard.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class Poll {

    public static final int MAX_ANSWERS = 4;
    public static final int TOTAL_SEGMENTS = 10;

    @SerializedName("_id")
    private final String id;
    private final long creatorId;
    private final long endsAt;
    private final String question;
    private final String description;
    private final Map<Long, Integer> respondents;
    private final List<String> answers;

    private long guildId;
    private long channelId;
    private long messageId;

    private Poll(Builder builder) {
        this(builder.creatorId, builder.endsAt, builder.question, builder.description);
        answers.addAll(builder.answers);
    }

    public Poll(long creatorId, long endsAt, String question, String description) {
        this.id = UUID.randomUUID().toString();
        this.creatorId = creatorId;
        this.endsAt = endsAt;
        this.question = question;
        this.description = description;

        respondents = new HashMap<>();
        answers = new ArrayList<>();
    }

    public static MongoCollection<Document> getCollection() {
        return Vanguard.getInstance().getDatabase().getCollection("polls");
    }

    public boolean hasEnded() {
        return endsAt - System.currentTimeMillis() <= 0;
    }

//    public void saveAsync() {
//        CompletableFuture.runAsync(() -> getCollection().replaceOne(
//                Filters.eq(id),
//                Document.parse(JsonUtil.writeToJson(this)),
//                new ReplaceOptions().upsert(true)
//        ));
//    }

    public Map<Integer, Set<Long>> getRespondentsByAnswer() {
        Map<Integer, Set<Long>> answerToRespondents = new HashMap<>();

        for (Long userId : respondents.keySet()) {
            Set<Long> members = new HashSet<>();
            int index = respondents.get(userId);

            if (answerToRespondents.containsKey(index)) {
                members = answerToRespondents.get(index);
            } else {
                answerToRespondents.put(index, members);
            }

            members.add(userId);
        }

        return answerToRespondents;
    }

    public static Builder builder(long creatorId) {
        return new Builder(creatorId);
    }

    public static class Builder {

        public static final long TTL = TimeUnit.MINUTES.toMillis(10L);

        private final long creatorId;
        private final long createdAt;
        private Long endsAt;
        private String question;
        private String description;
        private final List<String> answers;

        public Builder(long creatorId) {
            this.creatorId = creatorId;
            this.createdAt = System.currentTimeMillis();
            this.answers = new ArrayList<>();
        }

        public Builder endsAt(long endsAt) {
            this.endsAt = endsAt;
            return this;
        }

        public Builder question(String question) {
            this.question = question;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addAnswer(String answer) {
            if (answers.size() == MAX_ANSWERS)
                throw new IllegalStateException("This poll already has the maximum amount of answers.");

            answers.add(answer);
            return this;
        }

        public Builder removeAnswer() {
            if (answers.isEmpty())
                throw new IllegalStateException("There are no answers to remove from this poll.");

            answers.remove(answers.size() - 1);
            return this;
        }

        public MessageEmbed buildEmbed() {
            MessageEmbed.Field questionField = new MessageEmbed.Field(
                    "**Question**",
                    "`" + (question == null ? "Not Set" : question) + "`",
                    false
            );

            MessageEmbed.Field descriptionField = new MessageEmbed.Field(
                    "**Description**",
                    "`" + (description == null ? "Not Set" : description) + "`",
                    false
            );

            MessageEmbed.Field endTimeField = new MessageEmbed.Field(
                    "**End Time**",
                    "`" + (endsAt == null ? "Not Set`" :
                            TimeUtil.millisToString(endsAt) + "` (" + TimeUtil.millisToTimestamp(endsAt) + ")"),
                    false
            );

            MessageEmbed.Field answersField = new MessageEmbed.Field(
                    "**Answers**",
                    answers.isEmpty() ? "`None`" : IntStream.range(0, answers.size())
                            .mapToObj(index -> NumberEmote.getEmote(index) + " `" + answers.get(index) + "`")
                            .collect(Collectors.joining("\n")),
                    false
            );

            return EmbedUtil.createBuilder()
                    .setTitle(":bar_chart: | **Poll Builder**")
                    .setDescription("**Builder Expires:** " + TimeUtil.millisToTimestamp(createdAt + TTL))
                    .addField(questionField)
                    .addField(descriptionField)
                    .addField(endTimeField)
                    .addField(answersField)
                    .build();
        }

        public void updatePreview(IMessageEditCallback interaction) {
            if (interaction == null) {
                return;
            }

            interaction.editMessageEmbeds(buildEmbed()).queue();
        }

        public Poll build() {
            if (question == null)
                throw new IllegalStateException("A question has not been set for this poll.");

            if (endsAt == null)
                throw new IllegalStateException("An end date has not been set for this poll.");

            if (answers.size() < 2)
                throw new IllegalStateException("This poll must have at least two answers.");

            return new Poll(this);
        }
    }

}
