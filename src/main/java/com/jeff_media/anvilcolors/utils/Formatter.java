package com.jeff_media.anvilcolors.utils;

import com.jeff_media.anvilcolors.data.Color;
import com.jeff_media.anvilcolors.data.ItalicsMode;
import com.jeff_media.anvilcolors.data.RenameResult;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    private final Plugin plugin;

    private static final Pattern HEX_PATTERN = Pattern.compile("#([0-9a-fA-F]{6})");

    public Formatter(Plugin plugin) {
        this.plugin = plugin;
    }

    public RenameResult colorize(Permissible permissible, String input, ItalicsMode italicsMode) {

        int colors = 0;

        if (italicsMode == ItalicsMode.REMOVE) {
            input = ChatColor.RESET + input;
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
            output.append(input, lastIndex, matcher.start())
                    .append(ChatColor.of("#" + matcher.group(1)));
            if (italicsMode == ItalicsMode.FORCE) {
                output.append(ChatColor.ITALIC);
            }

            lastIndex = matcher.end();
        }
        if (lastIndex < input.length()) {
            output.append(input, lastIndex, input.length());
        }
        return new RenameResult(output.toString(), colors);
    }

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
