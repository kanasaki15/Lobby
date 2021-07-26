package xyz.n7mn.dev.lobby;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Lobby extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getServer().getPluginManager().registerEvents(new LobbyEvent(this), this);

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                getServer().broadcast(Component.text(ChatColor.YELLOW + "[ななみ鯖]"+ChatColor.RESET + "ここはロビー鯖です。 工事中です。"));
                getServer().broadcast(Component.text(ChatColor.YELLOW + "[ななみ鯖]"+ChatColor.RESET + "Discord : https://discord.gg/VWA3qQZzN3"));
            }
        };

        bukkitRunnable.runTaskTimerAsynchronously(this, 0L, 100L);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
