package xyz.n7mn.dev.lobby;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
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
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class LobbyEvent implements Listener {

    private final Plugin plugin;

    public LobbyEvent(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerJumpEvent (PlayerJumpEvent e){
        Player player = e.getPlayer();

        if (!player.isOp()){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerTeleportEvent (PlayerTeleportEvent e){
        Player player = e.getPlayer();

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
    public void PlayerMoveEvent (PlayerMoveEvent e){
        Player player = e.getPlayer();

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
    }
}
