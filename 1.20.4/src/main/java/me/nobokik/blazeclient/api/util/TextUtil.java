package me.nobokik.blazeclient.api.util;
import net.minecraft.text.TextContent;
import net.minecraft.text.Text;

import java.util.function.Predicate;

public class TextUtil {
    /**
     * A regex pattern to match a timestamp of HH:mm(:ss?).
     * It can be surrounded by any character.
     */
    private static final String TIMESTAMP_PATTERN = ".?\\d{1,2}:\\d{2}(:\\d{2})*.?";

    public static Text removeSiblings(Text parent, Predicate<Text> predicate) {
        var copy = parent.copy();
        copy.getSiblings().removeIf(predicate);

        return copy;
    }

    /**
     * Most mods add create a new text literal and append the original text to it.
     * This method removes the timestamp from the literal text content.
     */
    public static Text removeTimestamps(Text text) {
        var content = text.getContent();

        var string = content.toString();
        var withoutTimestamps = string.replaceAll(TIMESTAMP_PATTERN, "");
        if (withoutTimestamps.equals(string)) {
            return text;
        }

        var newText = Text.literal(withoutTimestamps.trim());
        newText.setStyle(newText.getStyle());
        newText.getSiblings().addAll(text.getSiblings());

        return newText;
    }
}