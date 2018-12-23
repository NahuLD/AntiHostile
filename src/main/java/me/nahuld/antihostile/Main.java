package me.nahuld.antihostile;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    private Set<UUID> notFollowed = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
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

        if (!player.hasPermission("antihostile.toggle")) {
            player.sendMessage(color("&cYou don't have enough permissions for this command."));
            return true;
        }

        UUID uniqueId = player.getUniqueId();

        if (notFollowed.contains(uniqueId)) {
            notFollowed.remove(uniqueId);
        } else {
            notFollowed.add(uniqueId);

            World world = player.getWorld();
            int x = player.getLocation().getChunk().getX();
            int z = player.getLocation().getChunk().getZ();
            for(int currentX = x - 6; currentX < x + 6; currentX++){
                for(int currentZ = z - 6; currentZ < z + 6; currentZ++){
                    Arrays.stream(world.getChunkAt(currentX, currentZ).getEntities())
                            .filter(entity -> entity instanceof Creature)
                            .map(entity -> (Creature) entity)
                            .filter(entity -> entity.getTarget() instanceof Player)
                            .filter(entity -> notFollowed.contains(entity.getTarget().getUniqueId()))
                            .forEach(entity -> entity.setTarget(null));
                }
            }
        }

        player.sendMessage(
                color("&aAnti hostile target has been set to &e" + notFollowed.contains(uniqueId) + " &afor you.")
        );

        return true;
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
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
