package xyz.n7mn.dev.lobby;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.Via;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import okhttp3.*;
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
import org.purpurmc.purpur.event.entity.MonsterEggSpawnEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class LobbyEvent implements Listener {

    private final Plugin plugin;

    private final String webhookUrl;
    private final String discordMsg = """
                    {
                      "username": "ななみ鯖ロビー",
                      "avatar_url": "https://7mi.site/icon/1.png",
                      "content": "#msg#"
                    }""";

    private final Map<Integer, String> protocolVersionList = new HashMap<>();


    public LobbyEvent(Plugin plugin){
        this.plugin = plugin;
        webhookUrl = plugin.getConfig().getString("DiscordWebhookURL");

        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://raw.githubusercontent.com/hugmanrique/mc-versions/main/versions.json")
                    .build();
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            JsonObject list = new Gson().fromJson(json, JsonObject.class);
            JsonElement editions = list.get("editions");
            JsonElement java = editions.getAsJsonObject().get("java");
            JsonArray versions = java.getAsJsonObject().get("versions").getAsJsonArray();

            boolean end = false;
            for (int i = 0; i < versions.size(); i++){
                String version = versions.get(i).getAsJsonObject().get("name").getAsString();

                int protocolNumber = 0;
                if (!versions.get(i).getAsJsonObject().get("protocolNumber").isJsonNull()){
                    protocolNumber = versions.get(i).getAsJsonObject().get("protocolNumber").getAsInt();

                    if (!end && versions.get(i).getAsJsonObject().get("protocolNumber").getAsInt() == 0){
                        end = true;
                    }

                    if (end && protocolNumber != 0){
                        break;
                    }
                };


                if (protocolVersionList.get(protocolNumber) != null){
                    version = version + "/" + protocolVersionList.get(protocolNumber);
                }
                protocolVersionList.put(protocolNumber, version);


            }


        } catch (Exception e){
            e.printStackTrace();
        }

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
    public void MonsterEggSpawnEvent(MonsterEggSpawnEvent e){
        if (!e.getPlayer().isOp()){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerJoinEvent (PlayerJoinEvent e) {
        new Thread(() -> {
            try {

                String ver = protocolVersionList.get(Via.getAPI().getPlayerVersion(e.getPlayer().getUniqueId()));
                String msg = discordMsg.replaceAll("#msg#",e.getPlayer().getName()+"さんが入室しました。(Ver: "+ver+" op持ち？ : "+e.getPlayer().isOp()+") ["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]");

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(msg, MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(webhookUrl)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                response.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }

        }).start();

        e.getPlayer().setGameMode(GameMode.CREATIVE);
        if (e.getPlayer().isOp()) {
            e.getPlayer().sendMessage(ChatColor.YELLOW + "[ななみ鯖]" + ChatColor.RESET + "「/book」でop持ってない人向けお知らせが見れるよ。");
            return;
        }

        e.getPlayer().teleport(plugin.getServer().getWorld("world").getSpawnLocation());
        LobbyBook.openBook(e.getPlayer(), plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuitEvent (PlayerQuitEvent e){
        new Thread(() -> {
            try {
                String msg = discordMsg.replaceAll("#msg#", Matcher.quoteReplacement(e.getPlayer().getName()+"さんが退出しました。"+" ["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]"));

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(msg, MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(webhookUrl)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                response.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }

        }).start();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncChatEvent (AsyncChatEvent e){

        new Thread(() -> {
            try {
                TextComponent textComponent = (TextComponent) e.message();
                String msg = discordMsg.replaceAll("#msg#",Matcher.quoteReplacement(e.getPlayer().getName()+" : "+textComponent.content()+" ["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]"));

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(msg, MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(webhookUrl)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                response.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }

        }).start();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e){
        new Thread(() -> {
            try {

                String msg = discordMsg.replaceAll("#msg#",e.getPlayer().getName()+" : "+e.getMessage()+" ["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]");

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(msg, MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(webhookUrl)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                response.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }

        }).start();

        if (e.getMessage().equals("/book") && e.getPlayer().isOp()){
            LobbyBook.openBook(e.getPlayer(), plugin);
            e.setCancelled(true);
            return;
        }

        if (e.getPlayer().isOp()){
            return;
        }

        String s = e.getMessage();
        if (s.startsWith("/plugins") || s.equals("/pl") || s.equals("/bukkit:plugins") || s.equals("/bukkit:pl")){
            e.getPlayer().sendMessage("Plugins (0): ");
            e.setCancelled(true);
            return;
        }

        if (s.startsWith("/version") || s.equals("/ver") || s.startsWith("/ver ") || s.startsWith("/bukkit:version") || s.equals("/bukkit:ver") || s.startsWith("/bukkit:ver ")){
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
