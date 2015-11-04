package Utils;

import DataModels.User.User;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

/**
 * Created by Scott on 10/31/15.
 */
public class Database {

    private static Database database = null;

    private String MYSQL_HOST = "us-cdbr-iron-east-03.cleardb.net";
    private String MYSQL_USERNAME = "b1f4e4b30642ef";
    private String MYSQL_PASSWORD = "e1468f68";
    private String MYSQL_DATABASE = "/heroku_ae5c2294800a104";

    private BoneCPConfig boneCPConfig = null;
    private BoneCP boneConnectionPool = null;

    public static Database getInstance() {
        if (null == database) {
            database = new Database();
            String databaseUrl = System.getenv("DATABASE_URL");
            if (null != databaseUrl) {
                try {
                    URI dbUri = new URI(databaseUrl);
                    database.MYSQL_HOST = dbUri.getHost();
                    database.MYSQL_USERNAME = dbUri.getUserInfo().split(":")[0];
                    database.MYSQL_PASSWORD = dbUri.getUserInfo().split(":")[1];
                    database.MYSQL_DATABASE = dbUri.getPath();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    database = null;
                    return null;
                }
            }
            try {
//                String dbUrl = "jdbc:mysql://" + database.MYSQL_HOST + "/"+ database.MYSQL_DATABASE
//                        + "?autoReconnect=true&failOverReadOnly=false&maxReconnects=10";
                String dbUrl = "jdbc:mysql://" + database.MYSQL_HOST + database.MYSQL_DATABASE + "?reconnect=true&autoReconnect=true";
//                String dbUrl = "jdbc:mysql://" + database.MYSQL_HOST + database.MYSQL_DATABASE;
                Class.forName("com.mysql.jdbc.Driver");
                database.boneCPConfig = new BoneCPConfig();
                database.boneCPConfig.setJdbcUrl(dbUrl);
                database.boneCPConfig.setUsername(database.MYSQL_USERNAME);
                database.boneCPConfig.setPassword(database.MYSQL_PASSWORD);
                database.boneCPConfig.setMinConnectionsPerPartition(5);
                database.boneCPConfig.setMaxConnectionsPerPartition(10);
                database.boneCPConfig.setPartitionCount(1);
                database.boneCPConfig.setConnectionTestStatement("SELECT 1");
                database.boneConnectionPool = new BoneCP(database.boneCPConfig);
            } catch (ClassNotFoundException|SQLException e) {
                e.printStackTrace();
                database = null;
                return null;
            }
        }
        return database;
    }

    public Connection getConnection() {
        try {
            return this.boneConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserWithEmail(String email) {
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from user where email = 444;");
                //iterate over the ResultSet
                while (resultSet.next()) {
                    //for each result, get the value of the column name
                    String res = resultSet.getString("email");
                    System.out.println(res);
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public boolean isUserExistInTheUserTable(User user) {
        if (null == user || null == user.getEmail() || user.getEmail().isEmpty()) return false;
        return isUserExistInTheUserTable(user.getEmail());
    }

    public boolean isUserExistInTheUserTable(String email) {
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from user where email = '" + email + "';");
                if (resultSet.next()) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try { if (null != connection) connection.close();} catch (SQLException e) { e.printStackTrace();}
            }
        }
        return false;
    }

    public boolean insertUserIntoUserTable(User user) {
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                String insertStatement =
                        "Insert Into `user` (`id`, `nickname`, `email`, `password`, `birthday`, `gender`, `fromCity`, `university`, `avatorUrl`, `token`, `isEmailVarified`) " +
                        "values " +
                        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
                preparedStatement.setNull(1, Types.INTEGER);
                preparedStatement.setString(2, user.getNickname());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getPassword());
                preparedStatement.setDate(5, null == user.getBirthday() ? null : new Date(user.getBirthday().getTime()));
                preparedStatement.setString(6, null == user.getGender() ? null : user.getGender().toString());
                preparedStatement.setString(7, user.getFromCity());
                preparedStatement.setString(8, user.getUniversity());
                preparedStatement.setString(9, null == user.getAvatorUrl()? null: user.getAvatorUrl().toString());
                preparedStatement.setString(10, user.getToken());
                preparedStatement.setBoolean(11, user.isEmailVarified());

                preparedStatement.execute();
//                ResultSet resultSet = statement.executeQuery("Insert Into `user` " +
//                        "(`id`, `nickname`, `email`, `password`, `birthday`, `gender`, `fromCity`, `university`, `avatorUrl`, `token`, `isEmailVarified`) " +
//                        "values" +
//                        " (NULL, " + user.getNickname() + ", " + user.getEmail() + ", " + user.getPassword() + ", " +
//                        new Date(user.getBirthday().getTime()) + ", " + user.getGender() + ", " + user.getFromCity() +
//                        ", " + user.getUniversity() + ", " + user.getAvatorUrl().toString() + ", NULL, false);");
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                try { if (null != connection) connection.close();} catch (SQLException e) { e.printStackTrace();}
            }
        }
        return true;
    }
}
