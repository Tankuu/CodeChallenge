package xyz.tanku.suggestionbox.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.tanku.suggestionbox.SuggestionBox;
import xyz.tanku.suggestionbox.sql.utils.SQLUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SuggestionCommand implements TabExecutor, Listener {

    private Player p = null;
    private String s = "";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Non-players can not make suggestions.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Suggestion can not be empty.");
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : args) { stringBuilder.append(str).append(" "); }
            String suggestion = stringBuilder.substring(0, stringBuilder.length() - 1) + ".";

            player.sendMessage(ChatColor.GREEN + "Make suggestion " + ChatColor.YELLOW + "\"" + suggestion + "\"" + ChatColor.GREEN + "?"
                    + "\nType \"y\" to confirm, and \"n\" to deny.");

            this.p = player;
            this.s = suggestion;

            Bukkit.getServer().getPluginManager().registerEvents(this, SuggestionBox.getInstance());
        }

        player.sendMessage(ChatColor.RED + "Suggestion can not be empty.");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        return Collections.emptyList();
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        if (event.getPlayer() != p) return;

        event.setCancelled(true);

        if (event.getMessage().equalsIgnoreCase("y")) {
            p.sendMessage(ChatColor.GREEN + "Suggestion sent.");
            try {
                makeSuggestion(p, s);
            } catch(Exception e) {
                e.printStackTrace();
            }
            HandlerList.unregisterAll(this);
        } else if (event.getMessage().equalsIgnoreCase("n")) {
            p.sendMessage(ChatColor.GREEN + "Suggestion cancelled.");
            HandlerList.unregisterAll(this);
        } else {
            p.sendMessage(ChatColor.RED + "Invalid arguments. Please type \"y\" to confirm, or \"n\" to deny confirmation of suggestion.");
        }
    }

    public void makeSuggestion(Player player, String suggestion) throws Exception {
        SQLUtils utils = new SQLUtils();

        utils.addSuggestion(player.getUniqueId().toString(), suggestion);
        notifyMods(player, suggestion);
    }

    public void notifyMods(Player suggester, String suggestion) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            if (!player.hasPermission("suggestionbox.mod.notify")) return;
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "New suggestion created by: " + ChatColor.YELLOW + suggester.getName() + ChatColor.GREEN + ":\n" + ChatColor.GOLD + "\"" + suggestion + "\"");
        }
    }
}