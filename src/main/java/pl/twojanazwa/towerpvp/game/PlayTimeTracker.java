package pl.twojanazwa.towerpvp.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.data.PlayerData;

public class PlayTimeTracker extends BukkitRunnable {

    private final TowerPVP plugin;

    public PlayTimeTracker(TowerPVP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Dodajemy minutę tylko graczom, którzy nie są na arenie
            if (plugin.getArenaManager().getArenaByPlayer(player.getUniqueId()) == null) {
                 PlayerData data = plugin.getStorageManager().getPlayerData(player.getUniqueId());
                 if (data != null) {
                     data.addPlayTimeMinute();
                 }
            }
        }
    }
}