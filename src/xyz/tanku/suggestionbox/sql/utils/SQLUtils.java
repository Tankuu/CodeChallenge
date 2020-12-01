package xyz.tanku.suggestionbox.sql.utils;

import xyz.tanku.suggestionbox.SuggestionBox;
import xyz.tanku.suggestionbox.sql.MySQLAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class SQLUtils {

    private PreparedStatement ps = null;

    private MySQLAccess mySQLAccess = SuggestionBox.getInstance().getMySQLAccess();

    public void addSuggestion(String uuid, String suggestion) throws Exception {
        String fixedUuid = getFixedUUID(uuid);
        Connection connection = mySQLAccess.getConnection();

        ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ? (suggestions TEXT) INSERT INTO ? (suggestions) VALUES ('?')");
        ps.setString(1, fixedUuid);
        ps.setString(2, fixedUuid);
        ps.setString(3, suggestion);

        ps.executeUpdate();

        connection.commit();
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
        ps = connection.prepareStatement("SHOW TABLES");

        List<String> l = new ArrayList<>();
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            l.add(set.getString("Tables_in_suggestionbox"));
        }

        return l;
    }

    public List<String> getSuggestions(String uuid) throws Exception {
        Connection connection = mySQLAccess.getConnection();
        ps = connection.prepareStatement("SELECT suggestions FROM ?");
        ps.setString(1, uuid);

        List<String> l = new ArrayList<>();
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            l.add(set.getString("suggestions"));
        }

        return l;
    }

    public Map<String, List<String>> getAllSuggestions() throws Exception {
        Connection connection = mySQLAccess.getConnection();
        ps = connection.prepareStatement("SELECT suggestions FROM ?");

        List<String> tables = this.getTables();
        Map<String, List<String>> map = new HashMap<>();

        for (String str : tables) {
            ps.setString(1, str);
            ResultSet set = ps.executeQuery();
            List<String> l = new ArrayList<>();
            while (set.next()) {
                l.add(set.getString("suggestions"));
            }
            map.put(str.replace("_", "-"), l);
        }

        return map;
    }

}