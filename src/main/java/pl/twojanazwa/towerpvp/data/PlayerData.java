package pl.twojanazwa.towerpvp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private final UUID playerUuid;
    private int kills;
    private int deaths;
    private int wins;
    private double money;
    private long playTimeMinutes;
    private final List<String> ownedClasses;

    public PlayerData(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.money = 0.0;
        this.playTimeMinutes = 0;
        this.ownedClasses = new ArrayList<>();
    }

    // Gettery
    public UUID getPlayerUuid() { return playerUuid; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getWins() { return wins; }
    public double getMoney() { return money; }
    public long getPlayTimeMinutes() { return playTimeMinutes; }
    public List<String> getOwnedClasses() { return ownedClasses; }
    public boolean hasClass(String className) { return ownedClasses.contains(className.toLowerCase()); }

    // Modyfikatory
    public void addKill() { this.kills++; }
    public void addDeath() { this.deaths++; }
    public void addWin() { this.wins++; }
    public void addMoney(double amount) { this.money += amount; }
    public void removeMoney(double amount) { this.money -= amount; }
    public void addPlayTimeMinute() { this.playTimeMinutes++; }
    public void addClass(String className) { this.ownedClasses.add(className.toLowerCase()); }

    // Settery (do wczytywania danych z pliku)
    public void setKills(int kills) { this.kills = kills; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setWins(int wins) { this.wins = wins; }
    public void setMoney(double money) { this.money = money; }
    public void setPlayTimeMinutes(long minutes) { this.playTimeMinutes = minutes; }
    public void setOwnedClasses(List<String> classes) { this.ownedClasses.addAll(classes); }
}