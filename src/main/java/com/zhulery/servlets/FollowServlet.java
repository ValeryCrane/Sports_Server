package com.zhulery.servlets;

import com.zhulery.dbworkers.DBWorker;
import com.zhulery.dbworkers.RelationsWorker;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FollowServlet extends HttpServlet {
    private final DBWorker worker;

    public FollowServlet(DBWorker worker) {
        this.worker = worker;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String token = req.getHeader("token");
            Integer userId = Integer.parseInt(req.getParameter("userId"));
            new RelationsWorker(worker).follow(userId, token);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception exception) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("text/plain");
            resp.getWriter().println(exception.getMessage());
        }
    }
}
