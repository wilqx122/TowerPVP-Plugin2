package pl.twojanazwa.towerpvp.ranking;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import pl.twojanazwa.towerpvp.TowerPVP;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RankingManager {
    private final TowerPVP plugin;
    private final File dataFolder;

    private List<Map.Entry<String, Integer>> topKills = new ArrayList<>();
    private List<Map.Entry<String, Integer>> topWins = new ArrayList<>();
    private List<Map.Entry<String, Double>> topMoney = new ArrayList<>();

    public RankingManager(TowerPVP plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        startUpdateTask();
    }

    private void startUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateRankings();
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 10, 20L * 60 * 5); // Start po 10 sek, powtarzaj co 5 minut
    }

    public void updateRankings() {
        plugin.getLogger().info("Aktualizowanie rankingów w tle...");
        if (!dataFolder.exists() || dataFolder.listFiles() == null) {
            plugin.getLogger().warning("Folder z danymi graczy nie istnieje. Nie można zaktualizować rankingu.");
            return;
        }

        Map<String, Integer> allKills = new HashMap<>();
        Map<String, Integer> allWins = new HashMap<>();
        Map<String, Double> allMoney = new HashMap<>();

        for (File file : dataFolder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                String name = config.getString("player-name", "Nieznany");
                allKills.put(name, config.getInt("stats.kills"));
                allWins.put(name, config.getInt("stats.wins"));
                allMoney.put(name, config.getDouble("stats.money"));
            }
        }

        topKills = sort(allKills, false);
        topWins = sort(allWins, false);
        topMoney = sort(allMoney, true);
        plugin.getLogger().info("Rankingi zaktualizowane.");
    }

    private <T extends Comparable<T>> List<Map.Entry<String, T>> sort(Map<String, T> map, boolean isDouble) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, T>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Map.Entry<String, Integer>> getTopKills() { return topKills; }
    public List<Map.Entry<String, Integer>> getTopWins() { return topWins; }
    public List<Map.Entry<String, Double>> getTopMoney() { return topMoney; }
}