package pl.twojanazwa.towerpvp.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.arena.Arena;
import pl.twojanazwa.towerpvp.data.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameLoop extends BukkitRunnable {

    private final Arena arena;
    private int gameTimeLeft;
    private int itemGiveTimer;
    private int classItemTimer;
    private final Random random = new Random();
    private final List<ItemStack> randomItems = new ArrayList<>();

    public GameLoop(Arena arena) {
        this.arena = arena;
        this.gameTimeLeft = TowerPVP.getInstance().getConfig().getInt("game-settings.game-duration-minutes", 8) * 60;
        this.itemGiveTimer = 15;
        this.classItemTimer = 2 * 60;
        
        // Wczytujemy losowe itemy z configu
        List<String> itemStrings = TowerPVP.getInstance().getConfig().getStringList("random-items");
        for(String s : itemStrings) {
            String[] parts = s.split(":");
            Material material = Material.getMaterial(parts[0].toUpperCase());
            int amount = (parts.length > 1) ? Integer.parseInt(parts[1]) : 1;
            if (material != null) {
                randomItems.add(new ItemStack(material, amount));
            }
        }
    }

    @Override
    public void run() {
        if (arena.getPlayers().size() <= 1) {
            this.cancel();
            UUID winnerUuid = arena.getPlayers().isEmpty() ? null : arena.getPlayers().get(0);
            arena.endGame(winnerUuid);
            return;
        }

        if (gameTimeLeft <= 0) {
            this.cancel();
            arena.endGame(null);
            return;
        }

        if (itemGiveTimer <= 0) {
            itemGiveTimer = 15;
            if(!randomItems.isEmpty()) {
                ItemStack randomItem = randomItems.get(random.nextInt(randomItems.size()));
                arena.broadcastMessage(ChatColor.AQUA + "+1 Losowy przedmiot!");
                for (UUID uuid : arena.getPlayers()) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        p.getInventory().addItem(randomItem.clone());
                    }
                }
            }
        }

        if (classItemTimer == 0) {
            arena.broadcastMessage(ChatColor.GOLD + "Klasy aktywowane! Otrzymujesz przedmioty specjalne!");
            TowerPVP plugin = TowerPVP.getInstance();
            for (UUID uuid : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                PlayerData playerData = plugin.getStorageManager().getPlayerData(uuid);

                if (!playerData.getOwnedClasses().isEmpty()) {
                    String activeClass = playerData.getOwnedClasses().get(0);
                    player.sendMessage(ChatColor.AQUA + "Otrzymujesz przedmioty dla klasy: " + activeClass + "!");
                    List<String> classItems = plugin.getConfig().getStringList("classes." + activeClass + ".items");
                    for (String itemString : classItems) {
                        String command = "give " + player.getName() + " " + itemString;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                }
            }
            classItemTimer = -1;
        }

        gameTimeLeft--;
        itemGiveTimer--;
        if (classItemTimer > 0) {
            classItemTimer--;
        }
    }
}