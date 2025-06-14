package pl.twojanazwa.towerpvp.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.arena.Arena;
import pl.twojanazwa.towerpvp.arena.GameState;

import java.util.UUID;

public class Countdown extends BukkitRunnable {

    private final Arena arena;
    private int timeLeft;

    public Countdown(Arena arena) {
        this.arena = arena;
        this.timeLeft = TowerPVP.getInstance().getConfig().getInt("game-settings.countdown-seconds", 30);
    }

    public void start() {
        arena.setGameState(GameState.COUNTDOWN);
        this.runTaskTimer(TowerPVP.getInstance(), 0L, 20L);
    }

    @Override
    public void run() {
        int minPlayers = TowerPVP.getInstance().getConfig().getInt("game-settings.min-players-to-start", 2);
        if (arena.getPlayers().size() < minPlayers) {
            this.cancel();
            arena.setGameState(GameState.WAITING);
            arena.broadcastMessage(ChatColor.RED + "Za mało graczy! Odliczanie zostało przerwane.");
            // Tutaj można dodać logikę czyszczenia graczy z areny
            return;
        }

        if (timeLeft <= 0) {
            this.cancel();
            arena.startGame();
            return;
        }

        if (timeLeft <= 10) {
            String soundName = TowerPVP.getInstance().getConfig().getString("sounds.countdown-tick", "BLOCK_NOTE_BLOCK_PLING");
            Sound tickSound = Sound.valueOf(soundName.toUpperCase());

            for (UUID uuid : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendTitle(
                            ChatColor.GREEN + "Start za...",
                            ChatColor.YELLOW + "" + timeLeft + (timeLeft > 1 ? " sekund" : " sekundę"),
                            0, 25, 5
                    );
                    player.playSound(player.getLocation(), tickSound, 1.0f, 1.0f);
                }
            }
        }
        timeLeft--;
    }
}