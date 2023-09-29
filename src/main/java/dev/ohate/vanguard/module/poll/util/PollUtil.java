package dev.ohate.vanguard.module.poll.util;

import dev.ohate.vanguard.module.poll.models.NumberEmote;
import dev.ohate.vanguard.module.poll.models.Poll;
import dev.ohate.vanguard.module.poll.models.SegmentEmote;
import dev.ohate.vanguard.util.EmbedUtil;
import dev.ohate.vanguard.util.InteractionKey;
import dev.ohate.vanguard.util.StringUtil;
import dev.ohate.vanguard.util.TimeUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bson.types.ObjectId;

import java.util.*;

public class PollUtil {

    private static final String BAR_CHART = "\uD83D\uDCCA";

    public static Modal getSetQuestionModal(UUID uuid) {
        return Modal.create(createPollBuilderId(uuid, "setQuestion"), BAR_CHART + " | Set Poll Question")
                .addActionRow(TextInput.create("question", "Set Question", TextInputStyle.SHORT)
                        .setMinLength(1)
                        .setMaxLength(200)
                        .build())
                .build();
    }

    public static Modal getSetDescriptionModal(UUID uuid) {
        return Modal.create(createPollBuilderId(uuid, "setDescription"), BAR_CHART + " | Set Poll Description")
                .addActionRow(TextInput.create("description", "Set Description", TextInputStyle.SHORT)
                        .setMinLength(1)
                        .setMaxLength(200)
                        .build())
                .build();
    }

    public static Modal getSetEndTimeModal(UUID uuid) {
        return Modal.create(createPollBuilderId(uuid, "setEndTime"), BAR_CHART + " | Set Poll End Time")
                .addActionRow(TextInput.create("endTime", "Set End Time", TextInputStyle.SHORT).build())
                .build();
    }

    public static Modal getAddAnswerModal(UUID uuid) {
        return Modal.create(createPollBuilderId(uuid, "addAnswer"), BAR_CHART + " | Add Poll Answer")
                .addActionRow(TextInput.create("answer", "Add Answer", TextInputStyle.SHORT).build())
                .build();
    }

    public static List<LayoutComponent> getPollBuilderButtons(UUID id) {
        Button setQuestion = Button.secondary(createPollBuilderId(id, "setQuestion"), "Set Question");
        Button setDescription = Button.secondary(createPollBuilderId(id, "setDescription"), "Set Description");
        Button setEndTime = Button.secondary(createPollBuilderId(id, "setEndTime"), "Set End Time");

        Button addAnswer = Button.success(createPollBuilderId(id, "addAnswer"), "Add Answer");
        Button removeAnswer = Button.danger(createPollBuilderId(id, "removeAnswer"), "Remove Answer");
        Button createPoll = Button.primary(createPollBuilderId(id, "createPoll"), "Create Poll");

        return List.of(
                ActionRow.of(setQuestion, setDescription, setEndTime),
                ActionRow.of(addAnswer, removeAnswer, createPoll)
        );
    }

    public static String createPollId(String pollId, Object action) {
        return "poll:" + pollId + ":" + action.toString();
    }

    public static String createPollBuilderId(UUID pollBuilderId, String action) {
        return "pollBuilder:" + pollBuilderId + ":" + action;
    }

    public static MessageEmbed buildPollEmbed(Poll poll) {
        return EmbedUtil.createBuilder()
                .setTitle(":bar_chart: | **" + poll.getQuestion() + "**")
                .setDescription(buildDescription(poll))
                .build();
    }

    private static String buildDescription(Poll poll) {
        String description = poll.getDescription();
        StringBuilder builder = new StringBuilder(description == null ? "" : description + "\n").append("\n");

        Map<Integer, Set<Long>> respondentsByAnswer = poll.getRespondentsByAnswer();

        List<String> answers = poll.getAnswers();

        for (int index = 0; index < answers.size(); index++) {
            builder.append("**")
                    .append(answers.get(index))
                    .append("**")
                    .append("\n")
                    .append(NumberEmote.getEmote(index))
                    .append(" ");

            SegmentEmote segmentEmote = SegmentEmote.getEmote(index);

            int responses = respondentsByAnswer.getOrDefault(index, Collections.emptySet()).size();
            double percentage = (double) responses / poll.getRespondents().size();
            int segments = (int) (percentage * Poll.TOTAL_SEGMENTS);

            builder.append(buildProgressBar(segmentEmote, segments))
                    .append("\u2502")
                    .append(" **")
                    .append(Math.round(percentage * 100))
                    .append("%** (")
                    .append(responses)
                    .append(")\n\n");
        }

        return builder.append("**Poll By:** <@")
                .append(poll.getCreatorId()).append(">\n**Poll Ends:** ")
                .append(TimeUtil.millisToTimestamp(poll.getEndsAt()))
                .toString();
    }

    private static String buildProgressBar(SegmentEmote segmentEmote, int segments) {
        if (segments == 0) {
            return buildFullBar(SegmentEmote.BLACK);
        } else if (segments == Poll.TOTAL_SEGMENTS) {
            return buildFullBar(segmentEmote);
        }

        return segmentEmote.getLeft() +
                StringUtil.repeat(segmentEmote.getCenter(), segments - 1) +
                StringUtil.repeat(SegmentEmote.BLACK.getCenter(), Poll.TOTAL_SEGMENTS - segments - 1) +
                SegmentEmote.BLACK.getRight();
    }

    private static String buildFullBar(SegmentEmote segmentEmote) {
        return segmentEmote.getLeft() +
                StringUtil.repeat(segmentEmote.getCenter(), Poll.TOTAL_SEGMENTS - 2) +
                segmentEmote.getRight();
    }

}
