package pl.twojanazwa.towerpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.twojanazwa.towerpvp.TowerPVP;

public class AdminCommand implements CommandExecutor {

    private final TowerPVP plugin;

    public AdminCommand(TowerPVP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("towerpvp.admin")) {
            sender.sendMessage(ChatColor.RED + "Nie masz uprawnień do użycia tej komendy!");
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender, label);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Użycie: /" + label + " create <nazwa> <1vs1|solo>");
                    return true;
                }
                String arenaName = args[1];
                String arenaType = args[2].toLowerCase();
                if (!arenaType.equals("1vs1") && !arenaType.equals("solo")) {
                    sender.sendMessage(ChatColor.RED + "Nieprawidłowy typ areny. Dostępne typy: 1vs1, solo.");
                    return true;
                }
                plugin.getArenaManager().createArena(sender, arenaName, arenaType);
                break;

            case "setspawn":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Ta komenda może być wykonana tylko przez gracza.");
                    return true;
                }
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Użycie: /" + label + " setspawn <nazwa_areny> <numer>");
                    return true;
                }
                Player player = (Player) sender;
                String spawnArenaName = args[1];
                try {
                    int spawnIndex = Integer.parseInt(args[2]);
                    Location location = player.getLocation();
                    plugin.getArenaManager().setSpawnPoint(player, spawnArenaName, spawnIndex, location);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Numer spawnu musi być liczbą całkowitą!");
                }
                break;

            case "savearena":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Użycie: /" + label + " savearena <nazwa_areny>");
                    return true;
                }
                String saveArenaName = args[1];
                plugin.getArenaManager().saveArena(sender, saveArenaName);
                break;

            case "list":
                plugin.getArenaManager().listArenas(sender);
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Nieznana komenda.");
                sendHelpMessage(sender, label);
                break;
        }
        return true;
    }

    private void sendHelpMessage(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.GOLD + "--- Pomoc TowerPVP Admin ---");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " create <nazwa> <1vs1|solo> - Tworzy nową arenę.");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " setspawn <nazwa> <numer> - Ustawia spawn dla gracza.");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " savearena <nazwa> - Zapisuje stan areny (scenerię).");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " list - Wyświetla listę wszystkich aren.");
    }
}