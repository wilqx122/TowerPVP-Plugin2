package pl.twojanazwa.towerpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.twojanazwa.towerpvp.TowerPVP;
import pl.twojanazwa.towerpvp.arena.Arena;

public class PlayerCommand implements CommandExecutor {

    private final TowerPVP plugin;

    public PlayerCommand(TowerPVP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Ta komenda jest tylko dla graczy.");
            return true;
        }

        Player player = (Player) sender;
        Arena arena = plugin.getArenaManager().getArenaByPlayer(player.getUniqueId());

        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Nie jesteś na żadnej arenie!");
            return true;
        }

        if (arena.getSpectators().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + "Opuszczasz arenę...");
            
            arena.removeSpectator(player.getUniqueId());
            
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();

            Location lobbySpawn = plugin.getLobbySpawn();
            if (lobbySpawn != null) {
                player.teleport(lobbySpawn);
            } else {
                player.sendMessage(ChatColor.RED + "Lobby nie jest ustawione! Skontaktuj się z administratorem.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Możesz opuścić arenę tylko po śmierci!");
        }

        return true;
    }
}