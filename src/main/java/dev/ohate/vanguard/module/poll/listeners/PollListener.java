package dev.ohate.vanguard.module.poll.listeners;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.ohate.vanguard.util.*;
import dev.ohate.vanguard.module.poll.models.NumberEmote;
import dev.ohate.vanguard.module.poll.models.Poll;
import dev.ohate.vanguard.module.poll.util.PollUtil;
import dev.ohate.vanguard.store.cache.PollBuilderCache;
import dev.ohate.vanguard.store.cache.PollCache;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PollListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();

        if (!componentId.contains(":")) {
            return;
        }

        String identifier = componentId.split(":")[0];

        if (identifier.equals("poll")) {
            handlePollButton(event);
        }

        if (identifier.equals("pollBuilder")) {
            handlePollBuilderButton(event);
        }
    }

    private void handlePollButton(ButtonInteractionEvent event) {
        String[] idSegments = event.getComponentId().split(":");

        Poll poll = PollCache.getPoll(idSegments[1]);

        if (poll == null) {
            Reply.ephemeral(event, Lang.POLL_ENDED);
            return;
        }

        int index = Integer.parseInt(idSegments[2]);

        Reply.ephemeral(event, EmbedUtil.createBuilder()
                .setTitle(":bar_chart: | " + poll.getQuestion())
                .setDescription("Your answer has been updated to\n" + NumberEmote.getEmote(index) + " `" + poll.getAnswers().get(index) + "`")
                .build());

        poll.getRespondents().put(event.getMember().getIdLong(), index);
        PollCache.schedulePollUpdate(poll, event.getMessage());
    }

    private void handlePollBuilderButton(ButtonInteractionEvent event) {
        String[] idSegments = event.getComponentId().split(":");

        UUID uuid = UUID.fromString(idSegments[1]);
        String action = idSegments[2];

        Poll.Builder builder = PollBuilderCache.getPollBuilder(uuid);

        if (builder == null) {
            Reply.ephemeral(event, Lang.POLL_BUILDER_EXPIRED);
            return;
        }

        switch (action) {
            case "setQuestion" -> event.replyModal(PollUtil.getSetQuestionModal(uuid)).queue();
            case "setDescription" -> event.replyModal(PollUtil.getSetDescriptionModal(uuid)).queue();
            case "setEndTime" -> event.replyModal(PollUtil.getSetEndTimeModal(uuid)).queue();
            case "addAnswer" -> event.replyModal(PollUtil.getAddAnswerModal(uuid)).queue();
            case "removeAnswer" -> {
                try {
                    builder.removeAnswer();
                } catch (IllegalStateException e) {
                    event.replyEmbeds(EmbedUtil.createError(e.getMessage())).setEphemeral(true).queue();
                    return;
                }

                builder.updatePreview(event);
            }
            case "createPoll" -> {
                Poll poll;

                try {
                    poll = builder.build();
                } catch (IllegalStateException e) {
                    Reply.ephemeral(event, EmbedUtil.createError(e.getMessage()));
                    return;
                }

                // TODO -> Allow for more than 5?
                List<Button> buttons = new ArrayList<>();

                for (int index = 0; index < poll.getAnswers().size(); index++) {
                    buttons.add(Button.of(
                            ButtonStyle.SECONDARY,
                            PollUtil.createPollId(poll.getId(), index),
                            poll.getAnswers().get(index),
                            NumberEmote.getEmoji(index)
                    ));
                }

                PollCache.addPoll(poll);

                event.getChannel()
                        .sendMessageEmbeds(PollUtil.buildPollEmbed(poll))
                        .addActionRow(buttons)
                        .queue(message -> Reply.ephemeral(event, EmbedUtil.createBuilder()
                                .setTitle(":bar_chart: | **Poll Created**")
                                .setDescription("The poll has been successfully created!")
                                .build()
                        ));
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();

        if (modalId.startsWith("pollBuilder")) {
            handlePollBuilderModal(event);
        }
    }

    public void handlePollBuilderModal(ModalInteractionEvent event) {
        String[] idSegments = event.getModalId().split(":");

        UUID uuid = UUID.fromString(idSegments[1]);
        String action = idSegments[2];

        Poll.Builder builder = PollBuilderCache.getPollBuilder(uuid);

        if (builder == null) {
            Reply.ephemeral(event, Lang.POLL_BUILDER_EXPIRED);
            return;
        }

        switch (action) {
            case "setQuestion" -> builder.question(event.getValue("question").getAsString());
            case "setDescription" -> builder.description(event.getValue("description").getAsString());
            case "setEndTime" -> {
                Duration duration = Duration.fromString(event.getValue("endTime").getAsString());

                if (duration.isInvalid()) {
                    Reply.ephemeral(event, Lang.POLL_BUILDER_INVALID_DURATION);
                    return;
                }

                // Truncate to remove seconds
                builder.endsAt((System.currentTimeMillis() + duration.getValue()) / (60 * 1000) * (60 * 1000));
            }
            case "addAnswer" -> {
                try {
                    builder.addAnswer(event.getValue("answer").getAsString());
                } catch (IllegalStateException e) {
                    Reply.ephemeral(event, EmbedUtil.createError(e.getMessage()));
                }
            }
        }

        builder.updatePreview(event);
    }

}
