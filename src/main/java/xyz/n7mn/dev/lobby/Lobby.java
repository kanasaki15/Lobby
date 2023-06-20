package xyz.n7mn.dev.lobby;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
                    TextComponent text1 = Component.text("[ななみ鯖] ").color(TextColor.color(NamedTextColor.YELLOW));
                    text1 = text1.append(Component.text("ここはロビー鯖です。").color(TextColor.color(NamedTextColor.WHITE)));
                    TextComponent text2 = Component.text("[ななみ鯖] ").color(TextColor.color(NamedTextColor.YELLOW));
                    text2 = text2.append(Component.text("Discord : ").color(TextColor.color(NamedTextColor.WHITE)));
                    text2 = text2.append(Component.text(getConfig().getString("discordURL")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, getConfig().getString("discordURL"))));
                    player.sendMessage(text1);
                    player.sendMessage(text2);

                    System.gc();
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
