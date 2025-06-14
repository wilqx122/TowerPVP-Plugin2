package pl.twojanazwa.towerpvp.arena;

import com.onarandombox.multiversecore.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.twojanazwa.towerpvp.TowerPVP;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaManager {

    private final TowerPVP plugin;
    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager(TowerPVP plugin) {
        this.plugin = plugin;
    }

    public void createArena(CommandSender sender, String name, String type) {
        if (arenas.containsKey(name)) {
            sender.sendMessage(ChatColor.RED + "Arena o tej nazwie już istnieje!");
            return;
        }

        String worldName = "tpvp_arena_" + name;
        MVWorldManager worldManager = plugin.getMultiverse().getMVWorldManager();

        boolean success = worldManager.addWorld(
                worldName, World.Environment.NORMAL, null, World.WorldType.FLAT,
                false, "VoidGenerator", false
        );

        if (success) {
            Arena arena = new Arena(name, worldName, type);
            arenas.put(name, arena);
            sender.sendMessage(ChatColor.GREEN + "Pomyślnie stworzono arenę '" + name + "' typu " + type + ".");
            sender.sendMessage(ChatColor.YELLOW + "Świat areny to: " + worldName);
            sender.sendMessage(ChatColor.YELLOW + "Teraz przejdź do świata (/mv tp " + worldName + "), zbuduj scenerię i ustaw spawny.");
        } else {
            sender.sendMessage(ChatColor.RED + "Wystąpił błąd podczas tworzenia świata dla areny! Sprawdź konsolę.");
        }
    }

    public void setSpawnPoint(Player player, String arenaName, int index, Location location) {
        Arena arena = arenas.get(arenaName);
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Nie znaleziono areny o nazwie: " + arenaName);
            return;
        }
        if (!player.getWorld().getName().equals(arena.getWorldName())) {
            player.sendMessage(ChatColor.RED + "Musisz znajdować się w świecie tej areny (" + arena.getWorldName() + "), aby ustawić spawn!");
            return;
        }
        while (arena.getSpawnPoints().size() < index) {
            arena.getSpawnPoints().add(null);
        }
        arena.getSpawnPoints().set(index - 1, location);
        player.sendMessage(ChatColor.GREEN + "Ustawiono spawn " + index + " dla areny " + arenaName + " na Twojej pozycji.");
    }

    public void saveArena(CommandSender sender, String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "Nie znaleziono areny o nazwie: " + arenaName);
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Sceneria dla areny '" + arenaName + "' została pomyślnie zapisana!");
        sender.sendMessage(ChatColor.GRAY + "(Symulacja - wymaga integracji z WorldEdit API do zapisu schematów).");
    }

    public void listArenas(CommandSender sender) {
        if (arenas.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Nie ma jeszcze żadnych skonfigurowanych aren.");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "--- Lista Dostępnych Aren ---");
        for (Arena arena : arenas.values()) {
            sender.sendMessage(ChatColor.YELLOW + "- " + arena.getName() +
                    " (" + arena.getType() + ") | Stan: " +
                    ChatColor.AQUA + arena.getGameState());
        }
    }

    public void joinAvailableArena(Player player, String type) {
        arenas.values().stream()
                .filter(a -> a.getType().equalsIgnoreCase(type) && a.getGameState() == GameState.WAITING)
                .findFirst()
                .ifPresentOrElse(arena -> {
                    arena.addPlayer(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Dołączyłeś do areny " + arena.getName() + "!");
                    arena.broadcastMessage(ChatColor.YELLOW + player.getName() + " dołączył do gry! (" + arena.getPlayers().size() + "/" + (type.equals("1vs1") ? 2 : 8) + ")");

                    int minPlayers = plugin.getConfig().getInt("game-settings.min-players-to-start", 2);
                    if (arena.getPlayers().size() >= minPlayers) {
                        arena.startCountdown();
                    }
                }, () -> player.sendMessage(ChatColor.RED + "Brak wolnych aren tego typu. Spróbuj ponownie za chwilę."));
    }
    
    public Arena getArena(String name) {
        return arenas.get(name);
    }
    
    public Arena getArenaByPlayer(UUID uuid) {
        for (Arena arena : arenas.values()) {
            if (arena.getPlayers().contains(uuid) || arena.getSpectators().contains(uuid)) {
                return arena;
            }
        }
        return null;
    }

    public long getAvailableArenasCount(String type) {
        return arenas.values().stream()
                .filter(arena -> arena.getType().equalsIgnoreCase(type) && arena.getGameState() == GameState.WAITING)
                .count();
    }
}