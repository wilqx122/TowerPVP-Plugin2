package pl.twojanazwa.towerpvp;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.twojanazwa.towerpvp.commands.AdminCommand;
import pl.twojanazwa.towerpvp.commands.PlayerCommand;
import pl.twojanazwa.towerpvp.commands.ReloadCommand;
import pl.twojanazwa.towerpvp.commands.SetLobbyCommand;
import pl.twojanazwa.towerpvp.data.StorageManager;
import pl.twojanazwa.towerpvp.events.GameListener;
import pl.twojanazwa.towerpvp.events.GuiListener;
import pl.twojanazwa.towerpvp.events.LobbyListener;
import pl.twojanazwa.towerpvp.events.PlayerConnectionListener;
import pl.twojanazwa.towerpvp.game.PlayTimeTracker;
import pl.twojanazwa.towerpvp.ranking.RankingManager;

import org.bukkit.configuration.ConfigurationSection;

public final class TowerPVP extends JavaPlugin {

    private static TowerPVP instance;
    private MultiVerseCore multiverse;
    private static Permission perms = null;

    private ArenaManager arenaManager;
    private StorageManager storageManager;
    private RankingManager rankingManager;

    private String lobbyWorldName;
    private double winReward;
    private double killReward;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadPluginConfig();

        if (!hookMultiverse()) {
            getLogger().severe("Nie znaleziono pluginu MultiVerse-Core! Wyłączanie TowerPVP...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupPermissions()) {
            getLogger().warning("Nie wykryto pluginu Vault lub pluginu do permisji! Mnożniki nagród dla rang nie będą działać.");
        }

        initializeManagers();
        registerCommands();
        registerEvents();

        new PlayTimeTracker(this).runTaskTimer(this, 20L * 60, 20L * 60);

        getLogger().info("Plugin TowerPVP został pomyślnie załadowany i włączony!");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            storageManager.savePlayerData(player);
        }
        getLogger().info("Plugin TowerPVP został wyłączony.");
    }

    private void initializeManagers() {
        storageManager = new StorageManager(this);
        arenaManager = new ArenaManager(this);
        rankingManager = new RankingManager(this);
    }

    private void registerCommands() {
        getCommand("towerpvpadmin").setExecutor(new AdminCommand(this));
        getCommand("opusc").setExecutor(new PlayerCommand(this));
        getCommand("setlobby").setExecutor(new SetLobbyCommand(this));
        getCommand("tpreload").setExecutor(new ReloadCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new LobbyListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
    }

    public void loadPluginConfig() {
        this.lobbyWorldName = getConfig().getString("game-settings.lobby-world-name", "world");
        this.winReward = getConfig().getDouble("rewards.win-money", 100.0);
        this.killReward = getConfig().getDouble("rewards.kill-money", 10.0);
    }

    public void reloadPluginConfig() {
        reloadConfig();
        loadPluginConfig();
        // Można dodać powiadomienie o przeładowaniu dla innych menedżerów
        rankingManager.updateRankings();
    }

    private boolean hookMultiverse() {
        if (getServer().getPluginManager().getPlugin("MultiVerse-Core") != null) {
            multiverse = (MultiVerseCore) getServer().getPluginManager().getPlugin("MultiVerse-Core");
            return true;
        }
        return false;
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }

    public double calculateReward(Player player, double baseAmount) {
        Permission perms = getPermissions();
        if (perms == null) {
            return baseAmount;
        }
        double bestMultiplier = 1.0;
        ConfigurationSection multiplierSection = getConfig().getConfigurationSection("reward-multipliers");
        if (multiplierSection != null) {
            for (String rankKey : multiplierSection.getKeys(false)) {
                String permissionNode = multiplierSection.getString(rankKey + ".permission");
                if (permissionNode != null && perms.has(player, permissionNode)) {
                    double currentMultiplier = multiplierSection.getDouble(rankKey + ".multiplier");
                    if (currentMultiplier > bestMultiplier) {
                        bestMultiplier = currentMultiplier;
                    }
                }
            }
        }
        return baseAmount * bestMultiplier;
    }

    public static TowerPVP getInstance() { return instance; }
    public MultiVerseCore getMultiverse() { return multiverse; }
    public Permission getPermissions() { return perms; }
    public ArenaManager getArenaManager() { return arenaManager; }
    public StorageManager getStorageManager() { return storageManager; }
    public RankingManager getRankingManager() { return rankingManager; }
    public String getLobbyWorldName() { return lobbyWorldName; }
    public double getWinReward() { return winReward; }
    public double getKillReward() { return killReward; }

    public Location getLobbySpawn() {
        String path = "lobby-spawn-location.";
        if (getConfig().getString(path + "world") == null) {
            return null;
        }
        return new Location(
                Bukkit.getWorld(getConfig().getString(path + "world")),
                getConfig().getDouble(path + "x"),
                getConfig().getDouble(path + "y"),
                getConfig().getDouble(path + "z"),
                (float) getConfig().getDouble(path + "yaw"),
                (float) getConfig().getDouble(path + "pitch")
        );
    }
}