package pl.twojanazwa.towerpvp.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.data.PlayerData;
import pl.twojanazwa.towerpvp.gui.ClassShopGUI;

public class GuiListener implements Listener {

    private final TowerPVP plugin;

    public GuiListener(TowerPVP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String guiTitle = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        String arenaSelectorTitle = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("gui.arena-selector.title"));
        String shopEntryTitle = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("gui.shop-entry.title"));
        String classShopTitle = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("gui.class-shop.title"));
        String rankingTitle = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("gui.ranking-gui.title"));

        if (guiTitle.equals(arenaSelectorTitle)) {
            event.setCancelled(true);
            if (clickedItem.getType() == Material.GREEN_SHULKER_BOX) {
                player.closeInventory();
                plugin.getArenaManager().joinAvailableArena(player, "1vs1");
            } else if (clickedItem.getType() == Material.RED_SHULKER_BOX) {
                player.closeInventory();
                plugin.getArenaManager().joinAvailableArena(player, "solo");
            }
        } else if (guiTitle.equals(shopEntryTitle)) {
            event.setCancelled(true);
            String configuredMaterial = plugin.getConfig().getString("gui.shop-entry.item.material");
            if (clickedItem.getType() == Material.valueOf(configuredMaterial)) {
                ClassShopGUI.open(player);
            }
        } else if (guiTitle.equals(classShopTitle)) {
            event.setCancelled(true);
            String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            ConfigurationSection classesSection = plugin.getConfig().getConfigurationSection("classes");

            for (String key : classesSection.getKeys(false)) {
                String configDisplayName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("classes." + key + ".display-name")));
                if (displayName.equals(configDisplayName)) {
                    PlayerData data = plugin.getStorageManager().getPlayerData(player.getUniqueId());
                    double price = plugin.getConfig().getDouble("classes." + key + ".price");

                    if (data.hasClass(key)) {
                        player.sendMessage(ChatColor.RED + "Już posiadasz tę klasę!");
                    } else if (data.getMoney() >= price) {
                        data.removeMoney(price);
                        data.addClass(key);
                        player.sendMessage(ChatColor.GREEN + "Pomyślnie zakupiono klasę " + displayName + "!");
                        ClassShopGUI.open(player); // Refresh
                    } else {
                        player.sendMessage(ChatColor.RED + "Nie masz wystarczająco pieniędzy!");
                    }
                    return;
                }
            }
        } else if (guiTitle.equals(rankingTitle)) {
            event.setCancelled(true);
        }
    }
}