package org.royaldev.royalcommands.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.ConfManager;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

public class CmdSetSpawn implements CommandExecutor {

    RoyalCommands plugin;

    public CmdSetSpawn(RoyalCommands plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setspawn")) {
            if (!plugin.isAuthorized(cs, "rcmds.setspawn")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            ConfManager spawns = new ConfManager("spawns.yml");
            float pitch = p.getLocation().getPitch();
            float yaw = p.getLocation().getYaw();
            double x = p.getLocation().getX();
            double y = p.getLocation().getY();
            double z = p.getLocation().getZ();
            String w = p.getWorld().getName();
            p.getWorld().setSpawnLocation((int) x, (int) y, (int) z);
            spawns.setFloat(pitch, "spawns." + w + ".pitch");
            spawns.setFloat(yaw, "spawns." + w + ".yaw");
            spawns.setDouble(x, "spawns." + w + ".x");
            spawns.setDouble(y, "spawns." + w + ".y");
            spawns.setDouble(z, "spawns." + w + ".z");
            cs.sendMessage(ChatColor.BLUE + "The spawn point of " + ChatColor.GRAY + p.getWorld().getName() + ChatColor.BLUE + " is set.");
            return true;
        }
        return false;
    }

}
