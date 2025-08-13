package com.jeff_media.anvilcolors.listener;

import com.jeff_media.anvilcolors.AnvilColors;
import com.jeff_media.anvilcolors.data.RenameResult;
import com.jeff_media.anvilcolors.utils.Formatter;
import com.jeff_media.anvilcolors.utils.VersionUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

import java.util.List;
import java.util.Objects;

public class AnvilListener implements Listener {

    private final AnvilColors plugin;
    private final Formatter formatter;

    public AnvilListener(AnvilColors plugin) {
        this.plugin = plugin;
        this.formatter = new Formatter(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilRename(PrepareAnvilEvent event) {

        // just in case somehow multiple players are viewing the anvil
        List<HumanEntity> viewers = event.getViewers();
        if (viewers.size() != 1)
            return;
        HumanEntity humanEntity = viewers.getFirst();
        if (!(humanEntity instanceof Player player))
            return;

        if (!(player.hasPermission("anvilcolors.color") || player.hasPermission("anvilcolors.color.*"))) {
            return;
        }

        boolean allowFormatting = player.hasPermission("anvilcolors.format") ||
                player.hasPermission("anvilcolors.format.*");

        ItemStack item = event.getResult();

        if (item == null)
            return;
        if (!item.hasItemMeta())
            return;
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        if (!meta.hasDisplayName())
            return;

        event.getView();
        AnvilView anvilView = event.getView();
        String renameText = anvilView.getRenameText();
        if (renameText == null || renameText.isEmpty())
            return;
        boolean hasMiniMessageTags = Formatter.containsMiniMessageTags(renameText);
        String displayName;
        int replacedColors;

        RenameResult result = formatter.colorize(player, renameText, plugin.getItalicsMode());
        String processedText = result.getColoredName();
        replacedColors = result.getReplacedColorsCount();

        if (allowFormatting) {
            displayName = Formatter.miniMessageToLegacy(processedText);
        } else {
            displayName = Formatter.miniMessageToLegacyColorOnly(processedText);
        }

        if (replacedColors == 0 && hasMiniMessageTags) {
            if (allowFormatting) {
                displayName = Formatter.miniMessageToLegacy(renameText);
            } else {
                displayName = Formatter.miniMessageToLegacyColorOnly(renameText);
            }
            replacedColors = 1;
        }

        if (replacedColors == 0) {
            return;
        }
        if (VersionUtils.hasAnvilRepairCostSupport()) {
            int cost = plugin.getConfig().getInt("level-cost");
            int costMultiplier = plugin.getConfig().getBoolean("cost-per-color") ? replacedColors : 1;
            int totalCost = cost * costMultiplier;

            plugin.debug("Cost: " + cost);
            plugin.debug("Cost multiplier: " + costMultiplier);
            plugin.debug("Total cost: " + totalCost);
            plugin.debug("Repair cost: " + anvilView.getRepairCost());
            plugin.debug("Colors: " + replacedColors);

            if (totalCost > 0) {
                int newRepairCost = totalCost + anvilView.getRepairCost();
                anvilView.setRepairCost(newRepairCost);
            }
        }

        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        event.setResult(item);
    }
}
