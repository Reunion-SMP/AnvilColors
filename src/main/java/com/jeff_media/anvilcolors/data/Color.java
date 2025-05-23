package com.jeff_media.anvilcolors.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.*;

public enum Color {

    // Colors
    BLACK(Type.COLOR, '0', "black"),
    DARK_BLUE(Type.COLOR, '1', "dark_blue"),
    DARK_GREEN(Type.COLOR, '2', "dark_green"),
    DARK_AQUA(Type.COLOR, '3', "dark_aqua"),
    DARK_RED(Type.COLOR, '4', "dark_red"),
    DARK_PURPLE(Type.COLOR, '5', "dark_purple"),
    GOLD(Type.COLOR, '6', "gold"),
    GRAY(Type.COLOR, '7', "gray"),
    DARK_GRAY(Type.COLOR, '8', "dark_gray"),
    BLUE(Type.COLOR, '9', "blue"),
    GREEN(Type.COLOR, 'a', "green"),
    AQUA(Type.COLOR, 'b', "aqua"),
    RED(Type.COLOR, 'c', "red"),
    LIGHT_PURPLE(Type.COLOR, 'd', "light_purple"),
    YELLOW(Type.COLOR, 'e', "yellow"),
    WHITE(Type.COLOR, 'f', "white"),

    // Formats
    OBFUSCATED(Type.FORMAT, 'k', "obfuscated"),
    BOLD(Type.FORMAT, 'l', "bold"),
    STRIKETHROUGH(Type.FORMAT, 'm', "strikethrough"),
    UNDERLINE(Type.FORMAT, 'n', "underline"),
    ITALIC(Type.FORMAT, 'o', "italic"),
    RESET(Type.FORMAT, 'r', "reset");

    private static final List<Color> COLORS_AND_FORMATS;
    private static final MiniMessage MINI_MESSAGE;
    private static final LegacyComponentSerializer LEGACY_SERIALIZER;

    static {
        List<Color> colorsAndFormats = new ArrayList<>(Arrays.asList(values()));
        COLORS_AND_FORMATS = Collections.unmodifiableList(colorsAndFormats);

        MINI_MESSAGE = MiniMessage.miniMessage();
        LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build();

        // initialize each enum value's translation
        for (Color color : values()) {
            color.initTranslation();
        }
    }

    private final Type type;
    private final char colorChar;
    private final String colorCode;
    private String translation;
    private final String name;
    private final String permission;

    Color(Type type, char colorChar, String name) {
        this(type, colorChar, name, "anvilcolors." + type.permissionNode + "." + colorChar);
    }

    Color(Type type, char colorChar, String name, String permission) {
        this.type = type;
        this.colorChar = colorChar;
        this.colorCode = "&" + colorChar;
        this.translation = null; // will be initialized in static block
        this.name = name;
        this.permission = permission;
    }

    private void initTranslation() {
        // start serializing the color code
        Component component = LEGACY_SERIALIZER.deserialize(this.colorCode);
        this.translation = MINI_MESSAGE.serialize(component);
    }

    public String getPermission() {
        return permission;
    }

    public static List<Color> list() {
        return COLORS_AND_FORMATS;
    }

    public RenameResult transform(String input, boolean forceItalics) {
        int colors = 0;
        while (input.contains(colorCode)) {
            colors++;
            String replacement = translation;
            if (forceItalics) {
                replacement += "<italic>";
            }
            input = input.replaceFirst(colorCode, replacement);
        }
        return new RenameResult(input, colors);
    }

    private enum Type {
        COLOR("color"), FORMAT("format");

        private final String permissionNode;

        Type(String permissionNode) {
            this.permissionNode = permissionNode;
        }
    }
}
