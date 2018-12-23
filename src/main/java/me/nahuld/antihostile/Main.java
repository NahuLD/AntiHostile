package me.nahuld.antihostile;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    private Set<UUID> notFollowed = new HashSet<>();

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        notFollowed.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!command.getName().equals("antihostile")) return true;

        if (player.hasPermission("antihostile.toggle")) {
            player.sendMessage(color("&cYou don't have enough permissions for this command."));
            return true;
        }

        UUID uniqueId = player.getUniqueId();

        if (notFollowed.contains(uniqueId)) {
            notFollowed.remove(uniqueId);
        } else notFollowed.add(uniqueId);

        player.sendMessage(
                color("&aAnti hostile target has been set to &e" + notFollowed.contains(uniqueId) + " &afor you.")
        );

        return true;
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        Entity target = event.getTarget();
        if (!(target instanceof Player)) return;

        Player player = (Player) target;
        if (!notFollowed.contains(player.getUniqueId())) return;

        event.setCancelled(true);
        event.setTarget(null);
    }

    private String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
