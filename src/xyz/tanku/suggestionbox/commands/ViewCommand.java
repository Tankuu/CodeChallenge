package xyz.tanku.suggestionbox.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import xyz.tanku.suggestionbox.sql.utils.SQLUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ViewCommand implements TabExecutor {

    private SQLUtils utils = new SQLUtils();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return sendPlayerSuggestions(sender, args[0]);
        } else if (args.length == 0) {
            return sendAllSuggestions(sender);
        }
        return false;
    }

    private boolean sendAllSuggestions(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Listing current suggestions...");

        Map<String, List<String>> map = new HashMap<>();
        try {
            map = utils.getAllSuggestions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> set = map.keySet().stream().map(Object::toString).collect(Collectors.toList());

        for (int i = 0; i < map.size(); i++) {
            sender.sendMessage(ChatColor.GREEN + set.get(i) + ":\"" + Bukkit.getOfflinePlayer(UUID.fromString(set.get(i))).getName() + "\"");
            for (int i1 = 0; i1 < map.get(set.get(i)).size(); i1++) {
                sender.sendMessage(ChatColor.YELLOW + "> \"" + map.get(set.get(i)).get(i1) + "\"");
            }
            sender.sendMessage("\n");
        }
        return true;
    }

    private boolean sendPlayerSuggestions(CommandSender sender, String arg) {
        List<String> l = new ArrayList<>();
        try {
            l = utils.getTables();
        } catch(Exception e) {
            e.printStackTrace();
        }

        String uuid = SQLUtils.getFixedUUID(arg);
        if (!l.contains(uuid)) {
            sender.sendMessage(ChatColor.RED + "That player (UUID) has not made a suggestion.");
            return false;
        }

        List<String> suggestions = new ArrayList<>();
        try {
            suggestions = utils.getSuggestions(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sender.sendMessage(ChatColor.GREEN + "Listing suggestions from UUID...\n" + SQLUtils.getUUID(uuid) + ":\"" + Bukkit.getOfflinePlayer(UUID.fromString(SQLUtils.getUUID(uuid))).getName() + "\"");
        for (String str : suggestions) {
            sender.sendMessage(ChatColor.YELLOW + "> \"" + str + "\"");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> uuids = new ArrayList<>();

        List<String> l = new ArrayList<>();
        try {
            l = utils.getTables();
        } catch(Exception e) {
            e.printStackTrace();
        }
        for (String str : l) {
            uuids.add(str.replace("_", "-"));
        }
        if (args.length <= 1) {
            return uuids;
        }
        return Collections.emptyList();
    }
}