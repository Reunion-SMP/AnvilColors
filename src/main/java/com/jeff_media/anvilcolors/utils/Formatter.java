package com.jeff_media.anvilcolors.utils;

import com.jeff_media.anvilcolors.data.Color;
import com.jeff_media.anvilcolors.data.ItalicsMode;
import com.jeff_media.anvilcolors.data.RenameResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    private final Plugin plugin;

    private static final Pattern HEX_PATTERN = Pattern.compile("#([0-9a-fA-F]{6})");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();
    // we have to use the section serializer to ensure that the item in the anvil
    // inventory is displayed correctly
    private static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private static final MiniMessage COLOR_ONLY_MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    // we have to allow <reset> to make ItalicsMode.REMOVE work
                    // there is no way to only allow <italic>
                    .resolver(StandardTags.reset())
                    .resolver(StandardTags.gradient())
                    .resolver(StandardTags.rainbow())
                    .build())
            .build();

    public Formatter(Plugin plugin) {
        this.plugin = plugin;
    }

    public RenameResult colorize(Permissible permissible, String input, ItalicsMode italicsMode) {

        int colors = 0;

        if (italicsMode == ItalicsMode.REMOVE) {
            // since we can't add <!italic> if the player doesn't have the formatting
            // permission as COLOR_ONLY_MINI_MESSAGE doesn't allow decorations, we'll
            // instead just add a <reset> tag right before
            input = "<reset>" + input;
        }

        if (VersionUtils.hasHexColorSupport() && hasPermission(permissible, "anvilcolors.color.hex")) {
            RenameResult result = replaceHexColors(input, italicsMode);
            input = result.getColoredName();
            colors += result.getReplacedColorsCount();
        }

        for (Color color : Color.list()) {
            if (hasPermission(permissible, color.getPermission())) {
                RenameResult result = color.transform(input, italicsMode == ItalicsMode.FORCE);
                input = result.getColoredName();
                colors += result.getReplacedColorsCount();
            }
        }

        return new RenameResult(input, colors);
    }

    private boolean hasPermission(Permissible permissible, String permission) {
        return !plugin.getConfig().getBoolean("require-permissions") || permissible == null
                || permissible.hasPermission(permission);
    }

    public static RenameResult replaceHexColors(String input, ItalicsMode italicsMode) {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = HEX_PATTERN.matcher(input);
        int colors = 0;
        while (matcher.find()) {
            colors++;
            output.append(input, lastIndex, matcher.start());

            // add the hex color in MiniMessage format
            String hexColor = matcher.group(1);
            output.append("<#").append(hexColor).append(">");

            if (italicsMode == ItalicsMode.FORCE) {
                output.append("<italic>");
            }

            lastIndex = matcher.end();
        }
        if (lastIndex < input.length()) {
            output.append(input, lastIndex, input.length());
        }

        return new RenameResult(output.toString(), colors);
    }

    public static String colorize(String s) {
        // Convert legacy color codes to MiniMessage format
        Component component = LEGACY_SERIALIZER.deserialize(s);
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Converts a string with MiniMessage tags to legacy format with section signs
     * (§)
     *
     * @param input Text with MiniMessage tags
     * @return Text with legacy color codes
     */
    public static String miniMessageToLegacy(String input) {
        try {
            // Parse MiniMessage tags into a Component
            Component component = MINI_MESSAGE.deserialize(input);
            // Convert the Component back to legacy text
            return SECTION_SERIALIZER.serialize(component);
        } catch (Exception e) {
            // If there's an error parsing the MiniMessage tags, return the input unchanged
            return input;
        }
    }

    public static String miniMessageToLegacyColorOnly(String input) {

        try {
            // Parse MiniMessage tags into a Component
            Component component = COLOR_ONLY_MINI_MESSAGE.deserialize(input);
            // Convert the Component back to legacy text
            return SECTION_SERIALIZER.serialize(component);
        } catch (Exception e) {
            // If there's an error parsing the MiniMessage tags, return the input unchanged
            return input;
        }
    }

    /**
     * Checks if the input string contains valid MiniMessage tags
     *
     * @param input Text to check for MiniMessage tags
     * @return true if the text contains valid MiniMessage tags
     */
    public static boolean containsMiniMessageTags(String input) {
        // Simple check for angle brackets
        return input.matches(".*<[a-zA-Z0-9#_:-]+>.*");
    }
}
