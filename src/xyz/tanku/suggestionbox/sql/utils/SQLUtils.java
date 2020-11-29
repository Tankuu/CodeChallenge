package xyz.tanku.suggestionbox.sql.utils;

import xyz.tanku.suggestionbox.SuggestionBox;
import xyz.tanku.suggestionbox.sql.MySQLAccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class SQLUtils {

    private MySQLAccess mySQLAccess = SuggestionBox.getInstance().getMySQLAccess();

    public void addSuggestion(String uuid, String suggestion) throws Exception {
        String fixedUuid = getFixedUUID(uuid);
        Connection connection = mySQLAccess.getConnection();
        Statement s = connection.createStatement();

        s.execute("use " + SuggestionBox.getInstance().getSqlId());

        s.execute("CREATE TABLE IF NOT EXISTS " + fixedUuid + " (suggestions TEXT)");

        s.execute("INSERT INTO " + fixedUuid + " (suggestions) VALUES ('" + suggestion + "')");
    }

    public static String getFixedUUID(String uuid) {
        /*
        This function is used as SQL doesn't like when dashes are in the name of the tables.
         */
        return uuid.replace("-", "_");
    }

    public static String getUUID(String uuid) {
        /*
        This function is used as SQL doesn't like when dashes are in the name of the tables.
         */
        return uuid.replace("_", "-");
    }

    public List<String> getTables() throws Exception {
        Connection connection = mySQLAccess.getConnection();
        Statement s = connection.createStatement();

        s.execute("use " + SuggestionBox.getInstance().getSqlId());

        List<String> l = new ArrayList<>();
        ResultSet set = s.executeQuery("SHOW TABLES");
        while (set.next()) {
            l.add(set.getString("Tables_in_suggestionbox"));
        }

        return l;
    }

    public List<String> getSuggestions(String uuid) throws Exception {
        Connection connection = mySQLAccess.getConnection();
        Statement s = connection.createStatement();

        s.execute("use " + SuggestionBox.getInstance().getSqlId());

        List<String> l = new ArrayList<>();
        ResultSet set = s.executeQuery("SELECT suggestions FROM " + uuid);
        while (set.next()) {
            l.add(set.getString("suggestions"));
        }

        return l;
    }

    public Map<String, List<String>> getAllSuggestions() throws Exception {
        Connection connection = mySQLAccess.getConnection();
        Statement s = connection.createStatement();

        s.execute("use " + SuggestionBox.getInstance().getSqlId());

        List<String> tables = this.getTables();
        Map<String, List<String>> map = new HashMap<>();

        for (String str : tables) {
            ResultSet set = s.executeQuery("SELECT suggestions FROM " + str);
            List<String> l = new ArrayList<>();
            while (set.next()) {
                l.add(set.getString("suggestions"));
            }
            map.put(str.replace("_", "-"), l);
        }

        return map;
    }

}