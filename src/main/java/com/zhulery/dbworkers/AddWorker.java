package com.zhulery.dbworkers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhulery.models.User;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public record AddWorker(DBWorker worker) {

    public void addRide(String token, String name, String description, String sport,
                        JsonObject statistics, JsonArray route) throws SQLException, FileNotFoundException {
        User user = worker.getUser(token);
        worker.addRide(user, name, description, sport, statistics, route);
    }
}
