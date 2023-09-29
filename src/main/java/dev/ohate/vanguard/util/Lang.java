package dev.ohate.vanguard.util;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class Lang {

    public static MessageEmbed POLL_BUILDER_EXPIRED = EmbedUtil.createError("Your poll builder has expired.");
    public static MessageEmbed POLL_BUILDER_INVALID_DURATION = EmbedUtil.createError("""
            Invalid duration provided.
                        
            Example: `1d2h45m`
                        
            - **1d** represents for 1 Day.
            - **2h** represents 2 hours.
            - **45m** represents 45 minutes.
            """
    );

    public static MessageEmbed POLL_ENDED = EmbedUtil.createError("This poll has already ended.");

}
