package pl.twojanazwa.towerpvp.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.ranking.RankingManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RankingGUI {

    public static void open(Player player) {
        RankingManager rankingManager = TowerPVP.getInstance().getRankingManager();
        String title = ChatColor.translateAlternateColorCodes('&', TowerPVP.getInstance().getConfig().getString("gui.ranking-gui.title"));
        Inventory gui = Bukkit.createInventory(null, 54, title);

        gui.setItem(11, createHeader("kills"));
        gui.setItem(13, createHeader("wins"));
        gui.setItem(15, createHeader("money"));

        fillColumn(gui, 20, rankingManager.getTopKills(), "Zabójstwa");
        fillColumn(gui, 22, rankingManager.getTopWins(), "Wygrane");
        fillColumn(gui, 24, rankingManager.getTopMoney(), "Monety", true);

        player.openInventory(gui);
    }

    private static ItemStack createHeader(String type) {
        String path = "gui.ranking-gui.headers." + type;
        Material material = Material.valueOf(TowerPVP.getInstance().getConfig().getString(path + ".material"));
        String name = ChatColor.translateAlternateColorCodes('&', TowerPVP.getInstance().getConfig().getString(path + ".name"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    private static void fillColumn(Inventory gui, int startIndex, List<?> topList, String valueName) {
        fillColumn(gui, startIndex, topList, valueName, false);
    }

    private static void fillColumn(Inventory gui, int startIndex, List<?> topList, String valueName, boolean isDouble) {
        for (int i = 0; i < topList.size() && i < 5; i++) { // Wyświetla top 5 w kolumnie
            Map.Entry<String, ?> entry = (Map.Entry<String, ?>) topList.get(i);
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + "#" + (i + 1) + " " + ChatColor.WHITE + entry.getKey());

            List<String> lore = new ArrayList<>();
            if (isDouble) {
                lore.add(ChatColor.GRAY + valueName + ": " + ChatColor.GOLD + String.format("%.2f", entry.getValue()));
            } else {
                lore.add(ChatColor.GRAY + valueName + ": " + ChatColor.GOLD + entry.getValue());
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(startIndex + (i * 9), item);
        }
    }
}