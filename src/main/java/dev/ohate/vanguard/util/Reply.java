package dev.ohate.vanguard.util;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class Reply {

    public static void ephemeral(IReplyCallback callback, MessageEmbed embed, MessageEmbed... embeds) {
        callback.replyEmbeds(embed, embeds).setEphemeral(true).queue();

    }

}
