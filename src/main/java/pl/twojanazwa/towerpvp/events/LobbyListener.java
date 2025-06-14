package pl.twojanazwa.towerpvp.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.gui.ArenaSelectorGUI;
import pl.twojanazwa.towerpvp.gui.RankingGUI;
import pl.twojanazwa.towerpvp.gui.ShopEntryGUI;

public class LobbyListener implements Listener {

    private final TowerPVP plugin;

    public LobbyListener(TowerPVP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!player.getWorld().getName().equalsIgnoreCase(plugin.getLobbyWorldName())) {
            return;
        }

        if (item == null || !item.hasItemMeta()) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        String strippedName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        if (item.getType() == Material.COMPASS && strippedName.equalsIgnoreCase("Wybierz Arene")) {
             event.setCancelled(true);
             ArenaSelectorGUI.open(player);
        } else if (item.getType() == Material.EMERALD && strippedName.equalsIgnoreCase("Sklep")) {
             event.setCancelled(true);
             ShopEntryGUI.open(player);
        } else if (item.getType() == Material.GOLD_INGOT && strippedName.equalsIgnoreCase("Ranking")) {
             event.setCancelled(true);
             RankingGUI.open(player);
        }
    }
}