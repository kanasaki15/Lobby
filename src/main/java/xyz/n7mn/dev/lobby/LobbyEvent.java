package xyz.n7mn.dev.lobby;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

public class LobbyEvent implements Listener {

    private final Plugin plugin;

    public LobbyEvent(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerTeleportEvent (PlayerTeleportEvent e){
        Player player = e.getPlayer();

        if (e.getTo().equals(plugin.getServer().getWorld("world").getSpawnLocation())){
            return;
        }

        if (!player.isOp()){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void InventoryOpenEvent (InventoryOpenEvent e){
        HumanEntity player = e.getPlayer();

        if (!player.isOp()){
            e.getInventory().close();
            e.getView().close();
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void InventoryClickEvent (InventoryClickEvent e){
        HumanEntity player = e.getWhoClicked();

        if (!player.isOp()){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void BlockBreakEvent  (BlockBreakEvent e){
        Player player = e.getPlayer();

        if (!player.isOp()){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void BlockCanBuildEvent (BlockCanBuildEvent e){
        Player player = e.getPlayer();

        if (!player.isOp()){
            e.setBuildable(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerJoinEvent (PlayerJoinEvent e){
        e.getPlayer().setGameMode(GameMode.CREATIVE);
        if (e.getPlayer().isOp()){
            e.getPlayer().sendMessage(ChatColor.YELLOW + "[ななみ鯖]" + ChatColor.RESET + "「/book」でop持ってない人向けお知らせが見れるよ。");
            return;
        }

        e.getPlayer().teleport(plugin.getServer().getWorld("world").getSpawnLocation());
        LobbyBook.openBook(e.getPlayer(), plugin);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e){

        if (e.getMessage().equals("/book") && e.getPlayer().isOp()){
            LobbyBook.openBook(e.getPlayer(), plugin);
            e.setCancelled(true);
            return;
        }

        if (e.getPlayer().isOp()){
            return;
        }

        String s = e.getMessage();
        if (s.startsWith("/plugins") || s.equals("/pl")){
            e.getPlayer().sendMessage("Plugins (0): ");
            e.setCancelled(true);
            return;
        }

        if (s.startsWith("/version") || s.equals("/ver") || s.startsWith("/ver ")){
            e.getPlayer().sendMessage("" +
                    "----- ななみ鯖 (ロビー) -----\n" +
                    "接続可能バージョン： 1.8 ～ " + plugin.getServer().getMinecraftVersion() + "\n" +
                    "動作システム     ： Purpur " + plugin.getServer().getMinecraftVersion()
            );
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerEggThrowEvent (PlayerEggThrowEvent e){
        if (!e.getPlayer().isOp()){
            e.setHatching(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerItemConsumeEvent (PlayerItemConsumeEvent e){
        if (!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }

}
