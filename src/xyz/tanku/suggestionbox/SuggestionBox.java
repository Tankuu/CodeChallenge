package xyz.tanku.suggestionbox;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.tanku.suggestionbox.commands.SuggestionCommand;
import xyz.tanku.suggestionbox.commands.ViewCommand;
import xyz.tanku.suggestionbox.sql.MySQLAccess;

public class SuggestionBox extends JavaPlugin {

    public static SuggestionBox instance;
    private String sqlId = getConfig().getString("mysql.database");
    private MySQLAccess mySQLAccess;

    public MySQLAccess getMySQLAccess() {
        return mySQLAccess;
    }

    public String getSqlId() {
        return sqlId;
    }

    public static SuggestionBox getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        String ip = this.getConfig().getString("mysql.ip");
        String user = this.getConfig().getString("mysql.user");
        String pass = this.getConfig().getString("mysql.password");

        mySQLAccess = new MySQLAccess(ip, user, pass, sqlId);

        this.getCommand("suggest").setExecutor(new SuggestionCommand());
        try {
            this.getCommand("view").setExecutor(new ViewCommand());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

}