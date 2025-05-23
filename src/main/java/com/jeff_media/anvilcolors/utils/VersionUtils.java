package com.jeff_media.anvilcolors.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.AnvilInventory;

public class VersionUtils {

    private static Boolean hasHexColorSupport = null;
    private static Boolean hasAnvilRepairCostSupport = null;

    public static boolean hasHexColorSupport() {
        if (hasHexColorSupport != null) {
            return hasHexColorSupport;
        }
        try {
            // Adventure's MiniMessage always supports hex colors
            MiniMessage.class.getDeclaredMethod("miniMessage");
            return hasHexColorSupport = true;
        } catch (NoSuchMethodException e) {
            return hasHexColorSupport = false;
        }
    }

    public static boolean hasAnvilRepairCostSupport() {
        if (hasAnvilRepairCostSupport != null) {
            return hasAnvilRepairCostSupport;
        }
        try {
            AnvilInventory.class.getDeclaredMethod("setRepairCost", int.class);
            return hasAnvilRepairCostSupport = true;
        } catch (NoSuchMethodException e) {
            return hasAnvilRepairCostSupport = false;
        }
    }

}
