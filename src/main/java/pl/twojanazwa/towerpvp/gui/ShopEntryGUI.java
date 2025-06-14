package pl.twojanazwa.towerpvp.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.twojanazwa.towerpvp.TowerPVP;

import java.util.ArrayList;
import java.util.List;

public class ShopEntryGUI {
    public static void open(Player player) {
        TowerPVP plugin = TowerPVP.getInstance();
        String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("gui.shop-entry.title", "&2&lSklep"));
        Inventory gui = Bukkit.createInventory(null, 9, title);
        String path = "gui.shop-entry.item.";

        Material material = Material.valueOf(plugin.getConfig().getString(path + "material", "NETHERITE_SWORD"));
        String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path + "name"));
        boolean enchanted = plugin.getConfig().getBoolean(path + "enchanted");

        ItemStack classesItem = new ItemStack(material);
        ItemMeta meta = classesItem.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        for(String line : plugin.getConfig().getStringList(path + "lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(lore);

        if(enchanted) {
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        classesItem.setItemMeta(meta);

        gui.setItem(4, classesItem);
        player.openInventory(gui);
    }
}