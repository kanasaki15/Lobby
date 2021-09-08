package xyz.n7mn.dev.lobby;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LobbyBook {

    public static void openBook(Player player, Plugin plugin) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("MySQLServer") + ":" + plugin.getConfig().getInt("MySQLPort") + "/" + plugin.getConfig().getString("MySQLDatabase") + plugin.getConfig().getString("MySQLOption"), plugin.getConfig().getString("MySQLUsername"), plugin.getConfig().getString("MySQLPassword"));
            con.setAutoCommit(true);

            PreparedStatement statement = con.prepareStatement("""
                        SELECT * FROM BanList, ServerList
                        WHERE Active = 1
                          AND BanList.Area = ServerList.ServerCode
                          AND UserUUID = ?
                          AND EndDate > NOW()
                        ORDER BY BanID DESC"""
            );
            statement.setString(1, player.getUniqueId().toString());
            ResultSet set = statement.executeQuery();

            boolean isBan = false;
            List<String> list = new ArrayList<>();
            while (set.next()) {
                isBan = true;
                StringBuffer sb = new StringBuffer();
                sb.append("ID : ");
                sb.append(set.getInt("BanID"));
                sb.append("\n");
                sb.append("範囲 : ");
                sb.append(set.getString("ServerName"));
                sb.append("\n");
                sb.append("理由 :\n");
                sb.append(set.getString("Reason"));
                sb.append("\n");
                sb.append("有効期限:\n");
                sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(set.getTimestamp("EndDate")));
                list.add(sb.toString());
            }
            set.close();
            statement.close();
            con.close();

            if (isBan) {
                ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta meta = (BookMeta) stack.getItemMeta().clone();
                meta.setAuthor("ななみ鯖");
                meta.setTitle("ななみ鯖お知らせ");
                meta.setGeneration(BookMeta.Generation.ORIGINAL);
                meta.addPages(Component.text(""));
                meta.page(1, Component.text("あなたはBANされているため入れません。\n(理由は次のページ以降にあります。)"));

                int i = 2;
                for (String str : list) {
                    meta.addPages(Component.text(""));
                    meta.page(i, Component.text(str));
                    i++;
                }

                stack.setItemMeta(meta);
                player.openBook(stack);
            } else {
                ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta meta = (BookMeta) stack.getItemMeta().clone();
                meta.setAuthor("ななみ鯖");
                meta.setTitle("ななみ鯖お知らせ");
                meta.setGeneration(BookMeta.Generation.ORIGINAL);
                meta.addPages(Component.text(""));
                meta.addPages(Component.text(""));
                meta.page(1, Component.text(
                        """
                                ななみ鯖へようこそ
                                生活サーバーは1.16.4から
                                イベントサーバー(放送用)は1.8からです！

                                (次へ)
                                """
                ));
                meta.page(2, Component.text("""
                        生活サーバーへ行くには「/server sv」を
                        イベントサーバーへは「/server main」をチャット欄に入れてください。
                        ※ 放送中の場合は詳細は放送で聞いてください。
                        """
                ));
                stack.setItemMeta(meta);
                player.openBook(stack);
            }

        } catch (SQLException ex) {
            ex.fillInStackTrace();
        }
    }
}
