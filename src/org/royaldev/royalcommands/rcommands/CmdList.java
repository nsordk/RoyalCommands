package org.royaldev.royalcommands.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.AFKUtils;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Fix [AFK] not showing

public class CmdList implements CommandExecutor {

    static RoyalCommands plugin;

    public CmdList(RoyalCommands plugin) {
        CmdList.plugin = plugin;
    }

    public String getNumOnline(CommandSender cs) {
        int hid = plugin.getNumberVanished();
        int all = plugin.getServer().getOnlinePlayers().length;
        boolean canSeeVanished = plugin.isAuthorized(cs, "rcmds.seehidden");
        String numPlayers;
        if (canSeeVanished && hid > 0) numPlayers = (all - hid) + "/" + hid;
        else numPlayers = String.valueOf(all - hid);
        return ChatColor.BLUE + "There are currently " + ChatColor.GRAY + numPlayers + ChatColor.BLUE + " out of " + ChatColor.GRAY + plugin.getServer().getMaxPlayers() + ChatColor.BLUE + " players online.";
    }

    public static String getSimpleList(CommandSender cs) {
        Player[] pl = plugin.getServer().getOnlinePlayers();
        StringBuilder sb = new StringBuilder();
        for (Player p : pl) {
            if (plugin.isVanished(p) && plugin.isAuthorized(cs, "rcmds.seehidden")) {
                sb.append(ChatColor.GRAY);
                sb.append("[HIDDEN]");
                sb.append(ChatColor.RESET);
                sb.append(formatPrepend(p));
                sb.append(ChatColor.RESET);
                sb.append(", ");
            } else if (!plugin.isVanished(p)) {
                if (AFKUtils.isAfk(p)) sb.append(ChatColor.GRAY + "[AFK]");
                sb.append(formatPrepend(p));
                sb.append(ChatColor.RESET + ", ");
            }
        }
        if (sb.length() < 2) return "";
        return "Online Players: " + sb.toString().substring(0, sb.length() - 2);
    }

    public static String[] getGroupList(CommandSender cs) {
        Player[] pl = plugin.getServer().getOnlinePlayers();
        Map<String, List<String>> groups = new HashMap<String, List<String>>();
        StringBuilder sb = new StringBuilder();
        for (Player p : pl) {
            String group = RoyalCommands.permission.getPrimaryGroup(p);
            List<String> inGroup = (groups.containsKey(group)) ? groups.get(group) : new ArrayList<String>();
            if (plugin.isVanished(p) && plugin.isAuthorized(cs, "rcmds.seehidden"))
                inGroup.add(ChatColor.GRAY + "[HIDDEN]" + ChatColor.RESET + formatPrepend(p));
            else if (!plugin.isVanished(p)) inGroup.add(formatPrepend(p));
            groups.put(group, inGroup);
        }
        List<String> toRet = new ArrayList<String>();
        for (String group : groups.keySet()) {
            List<String> inGroup = groups.get(group);
            if (inGroup.size() < 1) continue;
            sb.append(groupPrepend(group));
            sb.append(ChatColor.RESET);
            sb.append(": ");
            for (String name : inGroup) {
                sb.append(name);
                sb.append(ChatColor.RESET);
                sb.append(", ");
            }
            if (sb.length() < 2) {
                sb = new StringBuilder();
                continue;
            }
            toRet.add(sb.toString().substring(0, sb.length() - 2));
            sb = new StringBuilder();
        }
        for (String s : toRet) if (s == null) toRet.remove(s);
        return toRet.toArray(new String[toRet.size()]);
    }

    public static String groupPrepend(String group) {
        String format = plugin.whoGroupFormat;
        try {
            format = format.replaceAll("(?i)\\{prefix\\}", RoyalCommands.chat.getGroupPrefix(plugin.getServer().getWorlds().get(0), group));
        } catch (Exception e) {
            format = format.replaceAll("(?i)\\{prefix\\}", "");
        }
        try {
            format = format.replaceAll("(?i)\\{suffix\\}", RoyalCommands.chat.getGroupSuffix(plugin.getServer().getWorlds().get(0), group));
        } catch (Exception e) {
            format = format.replaceAll("(?i)\\{suffix\\}", "");
        }
        format = format.replace("{group}", group);
        format = RUtils.colorize(format);
        return format;
    }

    public static String formatPrepend(Player p) {
        String format = plugin.whoFormat;
        try {
            format = format.replaceAll("(?i)\\{prefix\\}", RoyalCommands.chat.getPlayerPrefix(p));
        } catch (Exception e) {
            format = format.replaceAll("(?i)\\{prefix\\}", "");
        }
        try {
            format = format.replaceAll("(?i)\\{group\\}", RoyalCommands.permission.getPrimaryGroup(p));
        } catch (Exception e) {
            format = format.replaceAll("(?i)\\{group\\}", "");
        }
        try {
            format = format.replaceAll("(?i)\\{suffix\\}", RoyalCommands.chat.getPlayerSuffix(p));
        } catch (Exception e) {
            format = format.replaceAll("(?i)\\{suffix\\}", "");
        }
        format = format.replaceAll("(?i)\\{name\\}", p.getName());
        format = format.replaceAll("(?i)\\{dispname\\}", p.getDisplayName());
        format = RUtils.colorize(format);
        return format;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("list")) {
            if (!plugin.isAuthorized(cs, "rcmds.list")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            cs.sendMessage(getNumOnline(cs));
            if (plugin.simpleList) {
                String pList = getSimpleList(cs);
                if (pList.equals("")) return true;
                cs.sendMessage(pList);
            } else cs.sendMessage(getGroupList(cs));
            return true;
        }
        return false;
    }
}
