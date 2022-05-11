package com.zhulery.dbworkers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.zhulery.models.Relation;
import com.zhulery.models.Ride;
import com.zhulery.models.User;
import com.zhulery.responses.InfoResponse;
import com.zhulery.responses.SearchResponse;
import com.zhulery.responses.UserSearchResponse;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record InfoWorker(DBWorker worker) {

    public InfoResponse getInfo(Integer userId, String token) throws SQLException, FileNotFoundException {
        User user = worker.getUser(userId);
        User requesting = worker.getUser(token);
        if (user.id().equals(requesting.id())) {
            return new InfoResponse(
                    user.id(),
                    user.name(),
                    Relation.self.toString(),
                    user.email(),
                    user.height(),
                    user.weight(),
                    getLastWeekStatistics(user)
            );
        } else {
            return new InfoResponse(
                    user.id(),
                    user.name(),
                    worker.getRelation(requesting, user).toString(),
                    null,
                    null,
                    null,
                    getLastWeekStatistics(user)
            );
        }
    }

    public InfoResponse getInfo(String token) throws SQLException, FileNotFoundException {
        User user = worker.getUser(token);
        return new InfoResponse(
                user.id(),
                user.name(),
                Relation.self.toString(),
                user.email(),
                user.height(),
                user.weight(),
                getLastWeekStatistics(user)
        );
    }

    public SearchResponse getSubs(Integer userId) throws SQLException, FileNotFoundException {
        User user = worker.getUser(userId);
        List<User> subs = worker.getFollows(user);
        List<UserSearchResponse> response = new ArrayList<>();
        for (User sub: subs) {
            response.add(new UserSearchResponse(
                    sub.id(),
                    sub.name()
            ));
        }
        return new SearchResponse(response);
    }

    public SearchResponse getSubs(String token) throws SQLException, FileNotFoundException {
        return getSubs(worker.getUser(token).id());
    }

    private JsonElement getLastWeekStatistics(User user) throws SQLException, FileNotFoundException {
        JsonObject jsonResponse = new JsonObject();
        List<Ride> rides = worker.getRides(user);
        double distance = 0.0;
        double averageSpeed = 0.0;
        for (Ride ride : rides) {
            distance += ride.statistics().getAsJsonObject().get("distance").getAsDouble();
            averageSpeed += ride.statistics().getAsJsonObject().get("avg. speed").getAsDouble();
        }
        if (rides.size() != 0) {
            averageSpeed /= rides.size();
        }
        jsonResponse.add("distance", new JsonPrimitive(distance));
        jsonResponse.add("avg. speed", new JsonPrimitive(averageSpeed));
        return jsonResponse;
    }
}
