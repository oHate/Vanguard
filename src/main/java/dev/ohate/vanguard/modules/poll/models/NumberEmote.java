package dev.ohate.vanguard.modules.poll.models;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;

import java.util.Locale;

public enum NumberEmote {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN;

    public UnicodeEmoji getEmoji() {
        return Emoji.fromUnicode("U+" + (31 + ordinal()) + " U+FE0F U+20E3");
    }

    public String getEmote() {
        return ":" + name().toLowerCase(Locale.ENGLISH) + ":";
    }

    public static String getEmote(int index) {
        return values()[index].getEmote();
    }

    public static UnicodeEmoji getEmoji(int index) {
        return values()[index].getEmoji();
    }

}
