package xyz.n7mn.dev.lobby;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Lobby extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new LobbyEvent(this), this);

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()){
                    player.sendMessage(Component.text(ChatColor.YELLOW + "[ななみ鯖]"+ChatColor.RESET + "ここはロビー鯖です。"));
                    player.sendMessage(Component.text(ChatColor.YELLOW + "[ななみ鯖]"+ChatColor.RESET + "Discord : "+getConfig().getString("discordURL")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, getConfig().getString("discordURL"))));
                }
            }
        };

        bukkitRunnable.runTaskTimerAsynchronously(this, 0L, 1200L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
