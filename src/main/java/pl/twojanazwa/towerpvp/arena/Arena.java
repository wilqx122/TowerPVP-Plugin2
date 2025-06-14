package pl.twojanazwa.towerpvp.arena;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.game.Countdown;
import pl.twojanazwa.towerpvp.game.GameLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arena {

    private final String name;
    private final String worldName;
    private final String type;
    private final List<Location> spawnPoints;
    private final List<UUID> players;
    private final List<UUID> spectators = new ArrayList<>();

    private GameState gameState;
    private BukkitTask gameTask;

    public Arena(String name, String worldName, String type) {
        this.name = name;
        this.worldName = worldName;
        this.type = type;
        this.spawnPoints = new ArrayList<>();
        this.players = new ArrayList<>();
        this.gameState = GameState.WAITING;
    }

    public void broadcastMessage(String message) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
        for (UUID uuid : spectators) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    public void startCountdown() {
        if (this.gameState != GameState.WAITING) return;

        ItemStack leaveItem = new ItemStack(Material.REDSTONE);
        ItemMeta leaveMeta = leaveItem.getItemMeta();
        leaveMeta.setDisplayName(ChatColor.RED + "Opuść Grę");
        leaveItem.setItemMeta(leaveMeta);

        int spawnIndex = 0;
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (spawnIndex < spawnPoints.size()) {
                    player.teleport(spawnPoints.get(spawnIndex));
                    spawnIndex++;
                } else {
                    player.teleport(spawnPoints.get(0)); // Fallback
                }
                player.getInventory().clear();
                player.getInventory().setItem(8, leaveItem);
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20.0);
                player.setFoodLevel(20);

                Location loc = player.getLocation().getBlock().getLocation();
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        loc.clone().add(x, 2, z).getBlock().setType(Material.GLASS);
                        if (x != 0 || z != 0) {
                            loc.clone().add(x, 0, z).getBlock().setType(Material.GLASS);
                            loc.clone().add(x, 1, z).getBlock().setType(Material.GLASS);
                        }
                    }
                }
            }
        }

        Countdown countdown = new Countdown(this);
        this.setGameTask(countdown);
        countdown.start();
        broadcastMessage(ChatColor.GREEN + "Odliczanie do startu rozpoczęte!");
    }

    public void startGame() {
        this.setGameState(GameState.LIVE);
        broadcastMessage(ChatColor.GOLD + "START! Walcz o przetrwanie!");

        String soundName = TowerPVP.getInstance().getConfig().getString("sounds.game-start", "ENTITY_ENDER_DRAGON_GROWL");
        Sound startSound = Sound.valueOf(soundName.toUpperCase());

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getInventory().clear();
                player.playSound(player.getLocation(), startSound, 1.0f, 0.8f);

                Location loc = player.getLocation().getBlock().getLocation();
                for (int x = -1; x <= 1; x++) {
                    for (int y = 0; y <= 2; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (loc.clone().add(x, y, z).getBlock().getType() == Material.GLASS) {
                                loc.clone().add(x, y, z).getBlock().setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }

        GameLoop gameLoop = new GameLoop(this);
        this.setGameTask(gameLoop);
        gameLoop.runTaskTimer(TowerPVP.getInstance(), 0L, 20L);
    }

    public void endGame(UUID winnerUuid) {
        this.setGameState(GameState.ENDING);
        if (this.gameTask != null) {
            this.gameTask.cancel();
        }

        if (winnerUuid == null) {
            broadcastMessage(ChatColor.GOLD + "Koniec gry! Czas minął, ogłaszamy remis!");
        } else {
            Player winner = Bukkit.getPlayer(winnerUuid);
            if(winner != null) {
                broadcastMessage(ChatColor.GOLD + "Koniec gry! Wygrywa " + winner.getName() + "!");
                TowerPVP plugin = TowerPVP.getInstance();
                winner.getInventory().clear();

                plugin.getStorageManager().getPlayerData(winnerUuid).addWin();
                double finalReward = plugin.calculateReward(winner, plugin.getWinReward());
                plugin.getStorageManager().getPlayerData(winnerUuid).addMoney(finalReward);
                winner.sendMessage(ChatColor.GOLD + "+ " + String.format("%.2f", finalReward) + " monet za zwycięstwo!");
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Location lobbySpawn = TowerPVP.getInstance().getLobbySpawn();
                List<UUID> allPlayersInArena = new ArrayList<>(players);
                allPlayersInArena.addAll(spectators);

                for (UUID uuid : allPlayersInArena) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        p.setGameMode(GameMode.SURVIVAL);
                        p.getInventory().clear();
                        if (lobbySpawn != null) {
                            p.teleport(lobbySpawn);
                        } else {
                            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        }
                    }
                }
                players.clear();
                spectators.clear();
                
                reset();
            }
        }.runTaskLater(TowerPVP.getInstance(), 200L); // 10 sekund
    }

    public void reset() {
        this.setGameState(GameState.REGENERATING);
        // Tutaj logika resetu mapy (np. wczytanie schematu przez WorldEdit)
        // Po resecie:
        this.setGameState(GameState.WAITING);
    }

    public String getName() { return name; }
    public String getWorldName() { return worldName; }
    public String getType() { return type; }
    public List<Location> getSpawnPoints() { return spawnPoints; }
    public List<UUID> getPlayers() { return players; }
    public GameState getGameState() { return gameState; }
    public BukkitTask getGameTask() { return gameTask; }
    public List<UUID> getSpectators() { return spectators; }

    public void setGameState(GameState gameState) { this.gameState = gameState; }
    public void setGameTask(BukkitTask gameTask) { this.gameTask = gameTask; }

    public void addPlayer(UUID playerUuid) { this.players.add(playerUuid); }
    public void removePlayer(UUID playerUuid) { this.players.remove(playerUuid); }
    public void addSpectator(UUID playerUuid) { this.spectators.add(playerUuid); }
    public void removeSpectator(UUID playerUuid) { this.spectators.remove(playerUuid); }
}