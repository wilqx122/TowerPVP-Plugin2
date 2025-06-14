package pl.twojanazwa.towerpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.twojanazwa.towerpvp.TowerPVP;

public class ReloadCommand implements CommandExecutor {

    private final TowerPVP plugin;

    public ReloadCommand(TowerPVP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("towerpvp.admin")) {
            sender.sendMessage(ChatColor.RED + "Nie masz uprawnień!");
            return true;
        }

        plugin.reloadPluginConfig();

        sender.sendMessage(ChatColor.GREEN + "Konfiguracja pluginu TowerPVP została pomyślnie przeładowana!");
        return true;
    }
}