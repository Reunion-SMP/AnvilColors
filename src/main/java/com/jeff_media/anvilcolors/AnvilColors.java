package com.jeff_media.anvilcolors;

import com.jeff_media.anvilcolors.command.ReloadCommand;
import com.jeff_media.anvilcolors.data.ItalicsMode;
import com.jeff_media.anvilcolors.listener.AnvilListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilColors extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("anvilcolors").setExecutor(new ReloadCommand(this));
        getServer().getScheduler().runTaskLater(this, () -> {
            getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        }, 20);
    }

    public void debug(String text) {
        if (getConfig().getBoolean("debug", false)) {
            getLogger().info("[DEBUG] " + text);
        }
    }

    public ItalicsMode getItalicsMode() {
        switch (getConfig().getString("italics", "force").toLowerCase()) {
            case "force":
                return ItalicsMode.FORCE;
            case "remove":
                return ItalicsMode.REMOVE;
            default:
                return ItalicsMode.NONE;
        }
    }
}
