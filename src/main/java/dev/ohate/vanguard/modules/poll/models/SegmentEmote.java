package dev.ohate.vanguard.modules.poll.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SegmentEmote {
    RED(
            "<:PLRed:1156320541599338637>",
            "<:PCRed:1156320538390700104>",
            "<:PRRed:1156320542509506570>"
    ),

    BLUE(
            "<:PLBlue:1156347325610995724>",
            "<:PCBlue:1156347324042334269>",
            "<:PRBlue:1156347326915424288>"
    ),

    GREEN(
            "<:PLGreen:1156347877908557897>",
            "<:PCGreen:1156347876729954444>",
            "<:PRGreen:1156347879246532678>"
    ),

    YELLOW(
            "<:PLYellow:1156347911026782218>",
            "<:PCYellow:1156347909428744324>",
            "<:PRYellow:1156347912436060233>"
    ),

    BLACK(
            "<:PLBlack:1156320505125670982>",
            "<:PCBlack:1156320502823014430>",
            "<:PRBlack:1156320506719502407>"
    );

    private final String left;
    private final String center;
    private final String right;

    public static SegmentEmote getEmote(int index) {
        return values()[index];
    }

}
