package com.jeff_media.anvilcolors.utils;

import org.bukkit.inventory.AnvilInventory;

public class VersionUtils {

    private static Boolean hasAnvilRepairCostSupport = null;

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
