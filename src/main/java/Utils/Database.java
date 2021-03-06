package Utils;

import DataModels.Business.Business;
import DataModels.Review.Review;
import DataModels.User.User;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Date;

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
                String dbUrl = "jdbc:mysql://" + database.MYSQL_HOST + database.MYSQL_DATABASE
                        + "?reconnect=true&autoReconnect=true&wait_timeout=28800&interactive_timeout=28800&maxReconnects=10";
//                String dbUrl = "jdbc:mysql://" + database.MYSQL_HOST + database.MYSQL_DATABASE;
                Class.forName("com.mysql.jdbc.Driver");
                database.boneCPConfig = new BoneCPConfig();
                database.boneCPConfig.setJdbcUrl(dbUrl);
                database.boneCPConfig.setUsername(database.MYSQL_USERNAME);
                database.boneCPConfig.setPassword(database.MYSQL_PASSWORD);
                database.boneCPConfig.setMinConnectionsPerPartition(2);
                database.boneCPConfig.setMaxConnectionsPerPartition(5);
                database.boneCPConfig.setPartitionCount(1);
//                database.boneCPConfig.setIdleConnectionTestPeriodInSeconds(80);
//                database.boneCPConfig.setConnectionTestStatement("SELECT 1");
//                database.boneCPConfig.setConnectionTimeout(8, TimeUnit.HOURS);
                database.boneConnectionPool = new BoneCP(database.boneCPConfig);
            } catch (ClassNotFoundException|SQLException e) {
                e.printStackTrace();
                database = null;
                return null;
            }
        } else {
            database.keepAlive();
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

    public User getUserWithToken(String token) throws SQLException {
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                String queryStatement =
                        "select user.id, user.email, user.nickname, user.birthday, user.gender, user.fromCity, user.university, user.avatorUrl from user INNER JOIN token on user.`id`=token.userID where token.token = '" + token + "';";
                PreparedStatement preparedStatement = connection.prepareStatement(queryStatement);
                if (true == preparedStatement.execute()) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    if (resultSet.next()) {
                        User user = new User();
                        user.setUserID(resultSet.getInt("id"));
                        user.setEmail(resultSet.getString("email"));
                        user.setNickname(resultSet.getString("nickname"));
                        user.setBirthday(resultSet.getDate("birthday"));
//                        user.setGender(resultSet.get("email"));
                        user.setFromCity(resultSet.getString("fromCity"));
                        user.setUniversity(resultSet.getString("university"));
//                        user.setAvatorUrl(URL(resultSet.getString("avatorUrl")));
                        return user;
                    }
                }
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }
        }
        return null;
    }

    /**-----Reviews-----*/
    public int insertReviewWith(int businessID, int userID, int starRating, String reviewText) throws SQLException {
        int reviewID = -1;
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                String insertStatement =
                        "Insert Into `review` (`businessID`, `userID`, `starRating`, `reviewText`, `reviewDate`) " +
                                "values " +
                                "(?, ?, ?, ?, ?);";
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, businessID);
                preparedStatement.setInt(2, userID);
                preparedStatement.setInt(3, starRating);
                preparedStatement.setString(4, reviewText);
                preparedStatement.setDate(5, new java.sql.Date(new java.util.Date().getTime()));//Auto generate current time.
                preparedStatement.execute();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reviewID = generatedKeys.getInt(1);
                        return reviewID;
                    } else {
                        throw new SQLException("Creating review failed, no ID obtained.");
                    }
                }
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }
        }
        return reviewID;
    }

    public ArrayList<Review> getReviewWith(int limit, int businessID, String userEmail, int userID, String sortBy) throws SQLException {
        ArrayList<Review> resultReviewss  = null;
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                String queryStatement =
                        "select user.email, user.nickname, review.starRating, review.reviewText, review.reviewDate, review.reviewVote from review inner join user on review.userID=user.id";
                if (businessID != -1) {
                    queryStatement += " where review.businessID='" + businessID + "'";
                    if (null != userEmail && !userEmail.isEmpty()) {
                        queryStatement += " and user.email='" + userEmail + "'";
                    } else if (userID != -1){
                        queryStatement += " and user.id='" + userID + "'";
                    }
                } else {
                    if (null != userEmail && !userEmail.isEmpty()) {
                        queryStatement += " where user.email='" + userEmail + "'";
                    } else if (userID != -1){
                        queryStatement += " where user.id='" + userID + "'";
                    }
                }

                if (null != sortBy && !sortBy.isEmpty()) {
                    if (sortBy.equalsIgnoreCase("vote")) {
                        queryStatement += " order by review.reviewVote";
                    }
                }

                queryStatement += " limit " + limit + ";";
                PreparedStatement preparedStatement = connection.prepareStatement(queryStatement, Statement.RETURN_GENERATED_KEYS);
                if (true == preparedStatement.execute()) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    resultReviewss = new ArrayList<>();
                    while (resultSet.next()) {
                        Review review = new Review();
                        review.setBusiness(new Business(businessID));
                        User user = new User();
                        user.setEmail(resultSet.getString("email"));
                        user.setNickname(resultSet.getString("nickname"));
                        review.setUser(user);
                        review.setStarRating(resultSet.getInt("starRating"));
                        review.setReviewText(resultSet.getString("reviewText"));
                        review.setDate(resultSet.getDate("reviewDate"));
                        review.setVotes(resultSet.getInt("reviewVote"));
                        resultReviewss.add(review);
                    }
                }
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }
        }
        return resultReviewss;
    }

    /**-----Reviews-----*/



    public User checkUserPasswordMatches(User user) throws SQLException {
        if (null == user || null == user.getEmail() || user.getEmail().isEmpty()) return null;
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from user where email = '" + user.getEmail() +
                        "' AND password = '" + user.getPassword() + "';");
                if (resultSet.next()) {
                    user.setUserID(resultSet.getInt("id"));
                } else {
                    return null;
                }
                resultSet = statement.executeQuery("Select * from token where userID = '" + user.getUserID() +
                        "' AND device = '" + user.getDevice() + "';");
                if (resultSet.next()) {
                    user.setToken(resultSet.getString("token"));
                } else {
                    String createTokenStatement =
                            "Insert Into `token` (`tokenID`, `userID`, `token`, `device`) " +
                                    "values " +
                                    "(?, ?, ?, ?);";
                    PreparedStatement preparedCreateTokenStatement = connection.prepareStatement(createTokenStatement);
                    preparedCreateTokenStatement.setNull(1, Types.INTEGER);
                    preparedCreateTokenStatement.setInt(2, user.getUserID());
                    preparedCreateTokenStatement.setString(3, user.getToken());
                    preparedCreateTokenStatement.setString(4, user.getDevice());
                    preparedCreateTokenStatement.execute();
                }
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }
        }
        return user;
    }

    public boolean isUserExistInTheUserTable(User user) throws SQLException {
        if (null == user || null == user.getEmail() || user.getEmail().isEmpty()) return false;
        return isUserExistInTheUserTable(user.getEmail());
    }

    public boolean isUserExistInTheUserTable(String email) throws SQLException {
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from user where email = '" + email + "';");
                if (resultSet.next()) {
                    return true;
                }
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }
        }
        return false;
    }

    public boolean insertUserIntoUserTable(User user) throws SQLException {
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                String insertStatement =
                        "Insert Into `user` (`id`, `nickname`, `email`, `password`, `birthday`, `gender`, `fromCity`, `university`, `avatorUrl`, `isEmailVarified`) " +
                        "values " +
                        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setNull(1, Types.INTEGER);
                preparedStatement.setString(2, user.getNickname());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getPassword());
                preparedStatement.setDate(5, null == user.getBirthday() ? null : new java.sql.Date(user.getBirthday().getTime()));
                preparedStatement.setString(6, null == user.getGender() ? null : user.getGender().toString());
                preparedStatement.setString(7, user.getFromCity());
                preparedStatement.setString(8, user.getUniversity());
                preparedStatement.setString(9, null == user.getAvatorUrl()? null: user.getAvatorUrl().toString());
                preparedStatement.setBoolean(10, user.isEmailVarified());

                preparedStatement.execute();
//                ResultSet resultSet = statement.executeQuery("Insert Into `user` " +
//                        "(`id`, `nickname`, `email`, `password`, `birthday`, `gender`, `fromCity`, `university`, `avatorUrl`, `token`, `isEmailVarified`) " +
//                        "values" +
//                        " (NULL, " + user.getNickname() + ", " + user.getEmail() + ", " + user.getPassword() + ", " +
//                        new Date(user.getBirthday().getTime()) + ", " + user.getGender() + ", " + user.getFromCity() +
//                        ", " + user.getUniversity() + ", " + user.getAvatorUrl().toString() + ", NULL, false);");
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserID(generatedKeys.getInt(1));
                        System.out.println(user.getUserID());
                        //insert token
                        String createTokenStatement =
                                "Insert Into `token` (`tokenID`, `userID`, `token`, `device`) " +
                                        "values " +
                                        "(?, ?, ?, ?);";
                        PreparedStatement preparedCreateTokenStatement = connection.prepareStatement(createTokenStatement);
                        preparedCreateTokenStatement.setNull(1, Types.INTEGER);
                        preparedCreateTokenStatement.setInt(2, user.getUserID());
                        preparedCreateTokenStatement.setString(3, user.getToken());
                        preparedCreateTokenStatement.setString(4, user.getDevice());
                        preparedCreateTokenStatement.execute();
                    } else {
                        //TODO: Remove user just added
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }
        }
        return true;
    }

    public ArrayList<Business> getBusinessWith(String city, int limit) throws SQLException  {
        ArrayList<Business> resultBusinesses  = null;
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                String queryStatement =
                        "select businessID, businessName, city, state, address, latitude, longitude, rating, categories, avatar from business";
                if (null != city && !city.isEmpty()) {
                    queryStatement += " where city = '" + city + "'";
                }
                queryStatement += " limit " + limit + ";";
                PreparedStatement preparedStatement = connection.prepareStatement(queryStatement, Statement.RETURN_GENERATED_KEYS);
                if (true == preparedStatement.execute()) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    resultBusinesses = new ArrayList<>();
                    while (resultSet.next()) {
                        Business business = new Business(resultSet.getInt("businessID"));
                        business.setBusinessName(resultSet.getString("businessName"));
                        business.setCity(resultSet.getString("city"));
                        business.setState(resultSet.getString("state"));
                        business.setAddress(resultSet.getString("address"));
                        business.setLatitude(resultSet.getDouble("latitude"));
                        business.setLongitude(resultSet.getDouble("longitude"));
                        business.setStarRating(resultSet.getFloat("rating"));
                        business.setCategories(resultSet.getString("categories"));
                        business.setPhotoURL(resultSet.getString("avatar"));
                        resultBusinesses.add(business);
                    }
                }
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }
        }
        return resultBusinesses;
    }

    public int insertBusinessWith(String businessName, String city, String address, int owner) throws SQLException {
        Connection connection = this.getConnection();
        int userID = -1;
        if (null != connection) {
            try {
                String insertStatement =
                        "Insert Into `business` (`businessName`, `city`, `address`, `owner`) " +
                                "values " +
                                "(?, ?, ?, ?);";
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, businessName);
                preparedStatement.setString(2, city);
                preparedStatement.setString(3, address);
                preparedStatement.setInt(4, owner);

                preparedStatement.execute();
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userID = generatedKeys.getInt(1);
                    } else {
                        //TODO: Remove user just added
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            } finally {
                if (null != connection) {
                    connection.close();
                }
            }
        }
        return userID;
    }

    private void keepAlive() {
        Connection connection = this.getConnection();
        if (null != connection) {
            try {
                Statement statement = connection.createStatement();
                statement.executeQuery("Select 1");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (null != connection) { try {connection.close();} catch (SQLException e) { e.printStackTrace();}}
            }
        }
    }
}
