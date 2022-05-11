package com.zhulery.servlets;

import com.google.gson.Gson;
import com.zhulery.dbworkers.AddWorker;
import com.zhulery.dbworkers.DBWorker;
import com.zhulery.requests.AddRideRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AddRideServlet extends HttpServlet {
    private final DBWorker worker;

    public AddRideServlet(DBWorker worker) {
        this.worker = worker;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        try {
            String token = req.getHeader("token");
            AddRideRequest addRideRequest = gson.fromJson(req.getReader(), AddRideRequest.class);
            new AddWorker(worker).addRide(
                    token,
                    addRideRequest.name,
                    addRideRequest.description,
                    addRideRequest.sport,
                    addRideRequest.statistics,
                    addRideRequest.route
            );
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception exception) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("text/plain");
            resp.getWriter().println(exception.getMessage());
        }
    }
}
