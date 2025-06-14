package pl.twojanazwa.towerpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.twojanazwa.towerpvp.TowerPVP;

public class SetLobbyCommand implements CommandExecutor {

    private final TowerPVP plugin;

    public SetLobbyCommand(TowerPVP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Ta komenda może być wykonana tylko przez gracza.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("towerpvp.admin")) {
            player.sendMessage(ChatColor.RED + "Nie masz uprawnień do użycia tej komendy!");
            return true;
        }

        Location location = player.getLocation();
        String path = "lobby-spawn-location.";

        plugin.getConfig().set(path + "world", location.getWorld().getName());
        plugin.getConfig().set(path + "x", location.getX());
        plugin.getConfig().set(path + "y", location.getY());
        plugin.getConfig().set(path + "z", location.getZ());
        plugin.getConfig().set(path + "yaw", location.getYaw());
        plugin.getConfig().set(path + "pitch", location.getPitch());

        plugin.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Pomyślnie ustawiono główny lobby spawn na Twojej aktualnej pozycji!");
        return true;
    }
}