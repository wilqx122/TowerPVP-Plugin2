package pl.twojanazwa.towerpvp.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.data.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class ClassShopGUI {

    public static void open(Player player) {
        TowerPVP plugin = TowerPVP.getInstance();
        String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("gui.class-shop.title", "&3&lSklep z Klasami"));
        Inventory gui = Bukkit.createInventory(null, 27, title);
        PlayerData playerData = plugin.getStorageManager().getPlayerData(player.getUniqueId());
        ConfigurationSection classesSection = plugin.getConfig().getConfigurationSection("classes");

        if (classesSection == null) {
            player.sendMessage(ChatColor.RED + "Sekcja klas nie jest skonfigurowana!");
            return;
        }

        for (String key : classesSection.getKeys(false)) {
            String path = "classes." + key;
            String displayName = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path + ".display-name"));
            Material material = Material.getMaterial(plugin.getConfig().getString(path + ".material", "STONE"));
            double price = plugin.getConfig().getDouble(path + ".price");
            List<String> lore = plugin.getConfig().getStringList(path + ".lore");

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);

            List<String> finalLore = new ArrayList<>();
            for (String line : lore) {
                finalLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            finalLore.add("");

            if (playerData.hasClass(key)) {
                finalLore.add(ChatColor.GREEN + "✔ Posiadasz tę klasę");
                if(plugin.getConfig().getBoolean("gui.class-shop.purchased-item-enchanted", true)) {
                    meta.addEnchant(Enchantment.LURE, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
            } else if (playerData.getMoney() >= price) {
                finalLore.add(ChatColor.YELLOW + "Cena: " + price + " monet");
                finalLore.add(ChatColor.AQUA + "Kliknij, aby kupić!");
            } else {
                finalLore.add(ChatColor.RED + "Cena: " + price + " monet");
                finalLore.add(ChatColor.DARK_RED + "Nie stać Cię na tę klasę!");
            }

            meta.setLore(finalLore);
            item.setItemMeta(meta);
            gui.addItem(item);
        }
        player.openInventory(gui);
    }
}