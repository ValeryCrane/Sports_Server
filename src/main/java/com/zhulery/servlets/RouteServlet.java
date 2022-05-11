package com.zhulery.servlets;

import com.google.gson.Gson;
import com.zhulery.dbworkers.DBWorker;
import com.zhulery.dbworkers.RidesWorker;
import com.zhulery.responses.RouteResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RouteServlet extends HttpServlet {
    private final DBWorker worker;

    public RouteServlet(DBWorker worker) {
        this.worker = worker;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        try {
            Integer rideId = Integer.parseInt(req.getParameter("rideId"));
            RouteResponse response = new RidesWorker(worker).getRoute(rideId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            gson.toJson(response, resp.getWriter());
        } catch (Exception exception) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("text/plain");
            resp.getWriter().println(exception.getMessage());
        }
    }
}
