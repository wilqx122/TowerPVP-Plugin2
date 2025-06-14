package pl.twojanazwa.towerpvp.events;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.arena.Arena;
import pl.twojanazwa.towerpvp.data.PlayerData;

public class GameListener implements Listener {

    private final TowerPVP plugin;
    private final ArenaManager arenaManager;

    public GameListener(TowerPVP plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Arena arena = arenaManager.getArenaByPlayer(victim.getUniqueId());

        if (arena == null) return;

        event.getDrops().clear();
        event.setDeathMessage("");

        PlayerData victimData = plugin.getStorageManager().getPlayerData(victim.getUniqueId());
        victimData.addDeath();

        Player killer = victim.getKiller();
        String deathMessage;
        if (killer != null) {
            deathMessage = ChatColor.RED + victim.getName() + " został zabity przez " + killer.getName() + "!";
            PlayerData killerData = plugin.getStorageManager().getPlayerData(killer.getUniqueId());
            killerData.addKill();
            
            double finalReward = plugin.calculateReward(killer, plugin.getKillReward());
            killerData.addMoney(finalReward);
            killer.sendMessage(ChatColor.GOLD + "+ " + String.format("%.2f", finalReward) + " monet za zabójstwo!");

            String soundName = plugin.getConfig().getString("sounds.player-kill", "ENTITY_EXPERIENCE_ORB_PICKUP");
            Sound killSound = Sound.valueOf(soundName.toUpperCase());
            killer.playSound(killer.getLocation(), killSound, 1.0f, 1.2f);
        } else {
            deathMessage = ChatColor.RED + victim.getName() + " umarł!";
        }
        arena.broadcastMessage(deathMessage);
        
        arena.removePlayer(victim.getUniqueId());
        arena.addSpectator(victim.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                victim.spigot().respawn();
                victim.setGameMode(GameMode.SPECTATOR);

                if (!arena.getSpawnPoints().isEmpty()) {
                    victim.teleport(arena.getSpawnPoints().get(0).clone().add(0, 5, 0));
                }
                victim.sendTitle(
                        ChatColor.RED + "ZGINĄŁEŚ!",
                        ChatColor.GRAY + "Wpisz /opusc aby wyjść",
                        10, 70, 20
                );
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onSpectatorClick(PlayerInteractEntityEvent event) {
        Player spectator = event.getPlayer();
        if (spectator.getGameMode() != GameMode.SPECTATOR) return;
        if (!(event.getRightClicked() instanceof Player)) return;

        Player target = (Player) event.getRightClicked();
        Arena spectatorArena = arenaManager.getArenaByPlayer(spectator.getUniqueId());
        Arena targetArena = arenaManager.getArenaByPlayer(target.getUniqueId());

        if (spectatorArena == null || !spectatorArena.equals(targetArena)) return;

        if (spectatorArena.getPlayers().contains(target.getUniqueId())) {
            spectator.setSpectatorTarget(target);
            spectator.sendMessage(ChatColor.GREEN + "Obserwujesz teraz " + target.getName() + ". Naciśnij [Shift], aby wyjść.");
        }
    }
}