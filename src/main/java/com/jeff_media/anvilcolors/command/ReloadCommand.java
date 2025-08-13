package com.jeff_media.anvilcolors.command;

import com.jeff_media.anvilcolors.utils.Formatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReloadCommand implements CommandExecutor {

    private final Plugin plugin;

    public ReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfig();
        // just being lazy here. I would replace the &a color code, but I'd rather
        // convert it using the miniMessage serializer than screw anything up
        String miniMessage = Formatter.colorize("&aAnvilColors reloaded");
        String legacyMessage = Formatter.miniMessageToLegacy(miniMessage);
        sender.sendMessage(legacyMessage);
        return true;

    }
}
