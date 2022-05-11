package com.zhulery.dbworkers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.zhulery.models.Relation;
import com.zhulery.models.Ride;
import com.zhulery.models.User;

import java.io.FileNotFoundException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class DBWorker {
    private static final String URL = "jdbc:mysql://localhost:3306/main";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "SQLep@Fyj_iLf3i";

    private final Connection connection;

    public DBWorker() throws SQLException {
        Driver driver = new com.mysql.cj.jdbc.Driver();
        DriverManager.registerDriver(driver);

        connection = DriverManager.getConnection(
                URL, USERNAME, PASSWORD
        );
    }

    public Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    public User getUser(Integer userId) throws SQLException, FileNotFoundException {
        String query = String.format("SELECT name, e_mail, height, weight, password, token " +
                "FROM users WHERE id = %d;", userId);
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return new User(
                        userId,
                        resultSet.getString("name"),
                        resultSet.getString("e_mail"),
                        resultSet.getDouble("height"),
                        resultSet.getDouble("weight"),
                        resultSet.getString("password"),
                        resultSet.getString("token")
                );
            } else {
                throw new FileNotFoundException("User not found!");
            }
        }
    }

    public User getUser(String token) throws SQLException, FileNotFoundException {
        return getUser(getUserId(token));
    }

    public User getUser(String email, String password) throws SQLException,
            IllegalAccessException, FileNotFoundException {
        String query = String.format("SELECT id, name, e_mail, height, weight, password, token " +
                "FROM users WHERE e_mail = \"%s\";", email);
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                if (resultSet.getString("password").equals(password)) {
                    return new User(
                            Integer.parseInt(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("e_mail"),
                            resultSet.getDouble("height"),
                            resultSet.getDouble("weight"),
                            resultSet.getString("password"),
                            resultSet.getString("token")
                    );
                } else {
                    throw new IllegalAccessException("Wrong password!");
                }
            } else {
                throw new FileNotFoundException("No user with such e-mail!");
            }
        }
    }

    public List<Ride> getRides(User user) throws SQLException, FileNotFoundException {
        String query = String.format("SELECT id, datetime, name, description, sport_id, statistics, route " +
                "FROM rides WHERE user_id = %d;", user.id());
        List<Ride> response = new ArrayList<>();
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                response.add(new Ride(
                        resultSet.getInt("id"),
                        resultSet.getString("datetime"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        getSportName(resultSet.getInt("sport_id")),
                        user.id(),
                        JsonParser.parseString(resultSet.getString("statistics")),
                        JsonParser.parseString(resultSet.getString("route")).getAsJsonArray()
                ));
            }
        }
        return response;
    }

    public Ride getRide(Integer rideId) throws SQLException, FileNotFoundException {
        String query = String.format("SELECT datetime, name, description, sport_id, " +
                "user_id, statistics, route FROM rides WHERE id = %d;", rideId);
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return new Ride(
                        rideId,
                        resultSet.getString("datetime"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        getSportName(resultSet.getInt("sport_id")),
                        resultSet.getInt("user_id"),
                        JsonParser.parseString(resultSet.getString("statistics")),
                        JsonParser.parseString(resultSet.getString("route")).getAsJsonArray()
                );
            } else {
                throw new FileNotFoundException("Ride not found!");
            }
        }
    }

    public List<User> getUsers(String searchQuery) throws SQLException, FileNotFoundException {
        List<User> response = new ArrayList<>();
        try {
            User user = getUser(Integer.parseInt(searchQuery));
            response.add(user);
        } catch (FileNotFoundException | NumberFormatException ignored) { }

        String query = String.format("SELECT id FROM users WHERE name LIKE \"%s%%\";", searchQuery);
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                response.add(getUser(resultSet.getInt("id")));
            }
        }
        return response;
    }

    public List<User> getFollows(User user) throws SQLException, FileNotFoundException {
        List<User> response = new ArrayList<>();
        String query = String.format("SELECT follows_id FROM follow_relations " +
                "WHERE follower_id = %d;", user.id());
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                response.add(getUser(resultSet.getInt("follows_id")));
            }
        }
        return response;
    }

    public User addUser(String name, String email, Double height, Double weight, String password)
            throws SQLException, IllegalAccessException, FileNotFoundException {
        if (isEmailTaken(email)) {
            throw new IllegalAccessException("Email is already taken!");
        }
        String token = generateToken();
        String query = String.format("INSERT INTO users(name, e_mail, height, weight, password, token) VALUES " +
                "(\"%s\", \"%s\", %f, %f, \"%s\", \"%s\");", name, email, height, weight, password, token);
        try (Statement statement = getStatement()) {
            statement.execute(query);
        }
        return getUser(token);
    }

    public void addRide(User user, String name, String description, String sport,
                        JsonElement statistics, JsonArray route) throws SQLException, FileNotFoundException {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String datetime = df.format(new Date());

        String query = String.format("INSERT INTO rides(name, description, sport_id, user_id, " +
                        "statistics, route, datetime) VALUES (\"%s\", \"%s\", %d, %d, '%s', '%s', '%s');",
                name, description, getSportId(sport), user.id(), statistics, route, datetime);

        try (Statement statement = getStatement()) {
            statement.execute(query);
        }
    }

    public Relation getRelation(User from, User to) throws SQLException {
        if (from.id().equals(to.id())) {
            return Relation.self;
        }
        String query = String.format("SELECT id FROM follow_relations " +
                "WHERE follower_id = %d AND follows_id = %d;", from.id(), to.id());
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return Relation.followed;
            } else {
                return Relation.unrelated;
            }
        }
    }

    public void setRelation(User from, User to, Relation relation) throws IllegalAccessException, SQLException {
        Relation currentRelation = getRelation(from, to);
        if (currentRelation == relation) {
            return;
        } else if (currentRelation == Relation.self) {
            throw new IllegalAccessException("You can't follow or unfollow yourself.");
        }
        switch (relation) {
            case self -> throw new IllegalAccessException("Can't relate different users as self");
            case followed -> {
                String query = String.format("INSERT INTO follow_relations(follower_id, follows_id) " +
                        "VALUES (%d, %d);", from.id(), to.id());
                try (Statement statement = getStatement()) {
                    statement.execute(query);
                }
            }
            case unrelated -> {
                String query = String.format("DELETE FROM follow_relations " +
                        "WHERE follower_id = %d AND follows_id = %d;", from.id(), to.id());
                try (Statement statement = getStatement()) {
                    statement.execute(query);
                }
            }
        }
    }

    private Integer getUserId(String token) throws SQLException, FileNotFoundException {
        String query = String.format("SELECT id FROM users WHERE token = \"%s\";", token);
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new FileNotFoundException("User not found! Troubles with authentication token!");
            }
        }
    }

    private String getSportName(Integer sportId) throws SQLException, FileNotFoundException {
        String query = String.format("SELECT name FROM sports WHERE id = \"%d\";", sportId);
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getString("name");
            } else {
                throw new FileNotFoundException("Sport not found!");
            }
        }
    }

    private Integer getSportId(String sportName) throws SQLException, FileNotFoundException {
        String query = String.format("SELECT id FROM sports WHERE name = \"%s\";", sportName);
        try (Statement statement = getStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new FileNotFoundException("Sport not found!");
            }
        }
    }

    public Boolean isTokenTaken(String token) throws SQLException {
        String query = String.format("SELECT id FROM users WHERE token = \"%s\";", token);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next();
        }
    }

    private Boolean isEmailTaken(String email) throws SQLException {
        String query = String.format("SELECT id FROM users WHERE e_mail = \"%s\";", email);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.next();
        }
    }

    private String generateToken() throws SQLException {
        String token = UUID.randomUUID().toString();
        while (isTokenTaken(token)) {
            token = UUID.randomUUID().toString();
        }
        return token;
    }
}
