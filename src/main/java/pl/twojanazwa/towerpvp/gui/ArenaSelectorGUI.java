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

public class ArenaSelectorGUI {

    public static void open(Player player) {
        TowerPVP plugin = TowerPVP.getInstance();
        String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("gui.arena-selector.title", "&1Wybierz Tryb Gry"));
        Inventory gui = Bukkit.createInventory(null, 27, title);

        ItemStack item1v1 = createGuiItem("1vs1", plugin.getArenaManager().getAvailableArenasCount("1vs1"));
        ItemStack itemSolo = createGuiItem("solo", plugin.getArenaManager().getAvailableArenasCount("solo"));

        gui.setItem(11, item1v1);
        gui.setItem(15, itemSolo);
        player.openInventory(gui);
    }

    private static ItemStack createGuiItem(String type, long amount) {
        TowerPVP plugin = TowerPVP.getInstance();
        String path = "gui.arena-selector.items." + type;

        Material material = Material.valueOf(plugin.getConfig().getString(path + ".material", "STONE"));
        String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path + ".name"));
        boolean enchanted = plugin.getConfig().getBoolean(path + ".enchanted");

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList(path + ".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("{amount}", String.valueOf(amount))));
        }
        meta.setLore(lore);

        if (enchanted) {
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }
}