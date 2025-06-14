package pl.twojanazwa.towerpvp.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.twojanazwa.towerpvp.TowerPVP;

public class PlayerConnectionListener implements Listener {

    private final TowerPVP plugin;

    public PlayerConnectionListener(TowerPVP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getStorageManager().loadPlayerData(player);

        Location lobbySpawn = plugin.getLobbySpawn();
        if (lobbySpawn != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleport(lobbySpawn);
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getStorageManager().savePlayerData(player);
        plugin.getStorageManager().unloadPlayerData(player.getUniqueId());
    }
}