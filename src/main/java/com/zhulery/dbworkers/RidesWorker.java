package com.zhulery.dbworkers;

import com.zhulery.models.Ride;
import com.zhulery.models.User;
import com.zhulery.responses.RideResponse;
import com.zhulery.responses.RidesResponse;
import com.zhulery.responses.RouteResponse;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record RidesWorker(DBWorker worker) {

    public RouteResponse getRoute(Integer rideId) throws SQLException, FileNotFoundException {
        Ride ride = worker.getRide(rideId);
        return new RouteResponse(ride.route());
    }

    public RidesResponse getRides(Integer userId) throws SQLException, FileNotFoundException {
        List<Ride> rides = worker.getRides(worker.getUser(userId));
        rides.sort(Comparator.comparing(Ride::datetime).reversed());
        return new RidesResponse(toRideResponses(rides));
    }

    public RidesResponse getRides(String token) throws SQLException, FileNotFoundException {
        List<Ride> rides = worker.getRides(worker.getUser(token));
        rides.sort(Comparator.comparing(Ride::datetime).reversed());
        return new RidesResponse(toRideResponses(rides));
    }

    public RidesResponse getFeed(String token) throws SQLException, FileNotFoundException {
        List<User> subs = worker.getFollows(worker().getUser(token));
        List<Ride> feed = new ArrayList<>();
        for (User sub : subs) {
            feed.addAll(worker.getRides(sub));
        }
        feed.sort(Comparator.comparing(Ride::datetime).reversed());
        return new RidesResponse(toRideResponses(feed));
    }

    private List<RideResponse> toRideResponses(List<Ride> rides) throws SQLException, FileNotFoundException {
        List<RideResponse> rideResponses = new ArrayList<>();
        for (Ride ride : rides) {
            rideResponses.add(new RideResponse(
                    ride.id(),
                    ride.userId(),
                    worker.getUser(ride.userId()).name(),
                    ride.name(),
                    ride.description(),
                    ride.sport(),
                    ride.datetime(),
                    ride.statistics()
            ));
        }
        return rideResponses;
    }
}
