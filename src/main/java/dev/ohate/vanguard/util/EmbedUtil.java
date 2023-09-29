package dev.ohate.vanguard.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

public class EmbedUtil {

    private static final Color TEAL = new Color(52, 211, 153);

    public static EmbedBuilder createBuilder() {
        return new EmbedBuilder()
                .setTimestamp(Instant.now())
                .setColor(TEAL);
    }

    public static MessageEmbed createError(String reason) {
        return createBuilder()
                .setTitle("An error has occurred")
                .setDescription(reason)
                .build();
    }

}
