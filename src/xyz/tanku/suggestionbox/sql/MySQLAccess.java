package xyz.tanku.suggestionbox.sql;

import java.sql.*;

public class MySQLAccess {

    private String ip;
    private String user;
    private String password;

    private String sqlId;

    private Connection connect = null;
    private Statement statement = null;

    public MySQLAccess(String ip, String user, String password, String sqlId) {
        this.ip = ip;
        this.user = user;
        this.password = password;
        this.sqlId = sqlId;
    }

    public Connection getConnection() throws Exception {
        Connection c;
        String connectUser = "user=" + user + "&password=" + password;

        String userConnection = "'operator'@'" + ip + "'";

        c = DriverManager
                .getConnection("jdbc:mysql://" + ip + "?" + connectUser);

        statement = c.createStatement();
        ResultSet resultSet = statement.executeQuery("SHOW DATABASES LIKE '" + sqlId + "'");
        if (!resultSet.next()) {
            statement.execute("CREATE DATABASE " + sqlId);
            statement.execute("USE " + sqlId);
            statement.execute("CREATE USER " + userConnection + " IDENTIFIED BY '" + password + "'");
            statement.execute("GRANT ALL PRIVILEGES ON * . * TO " + userConnection);
            statement.execute("GRANT ALL ON " + sqlId + ".* TO " + userConnection + " IDENTIFIED BY '"+ password + "' WITH GRANT OPTION");
            statement.execute("FLUSH PRIVILEGES");
        }
        connect = c;

        return c;
    }

}