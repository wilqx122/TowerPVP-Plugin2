package pl.twojanazwa.towerpvp.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.twojanazwa.towerpvp.TowerPVP;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageManager {

    private final TowerPVP plugin;
    private final File dataFolder;
    private final Map<UUID, PlayerData> playerDataCache = new HashMap<>();

    public StorageManager(TowerPVP plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        File playerFile = new File(dataFolder, uuid.toString() + ".yml");
        PlayerData playerData;

        if (playerFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            playerData = new PlayerData(uuid);
            playerData.setKills(config.getInt("stats.kills"));
            playerData.setDeaths(config.getInt("stats.deaths"));
            playerData.setWins(config.getInt("stats.wins"));
            playerData.setMoney(config.getDouble("stats.money"));
            playerData.setPlayTimeMinutes(config.getLong("stats.playtime-minutes"));
            playerData.setOwnedClasses(config.getStringList("owned-classes"));
        } else {
            playerData = new PlayerData(uuid);
        }
        playerDataCache.put(uuid, playerData);
    }

    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData playerData = playerDataCache.get(uuid);
        if (playerData == null) return;

        File playerFile = new File(dataFolder, uuid.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("player-name", player.getName());
        config.set("stats.kills", playerData.getKills());
        config.set("stats.deaths", playerData.getDeaths());
        config.set("stats.wins", playerData.getWins());
        config.set("stats.money", playerData.getMoney());
        config.set("stats.playtime-minutes", playerData.getPlayTimeMinutes());
        config.set("owned-classes", playerData.getOwnedClasses());

        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie udało się zapisać danych gracza " + player.getName());
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.get(uuid);
    }

    public void unloadPlayerData(UUID uuid) {
        playerDataCache.remove(uuid);
    }
}