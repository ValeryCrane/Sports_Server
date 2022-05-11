package com.zhulery.servlets;

import com.google.gson.Gson;
import com.zhulery.dbworkers.DBWorker;
import com.zhulery.dbworkers.InfoWorker;
import com.zhulery.responses.InfoResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class UserInfoServlet extends HttpServlet {
    private final DBWorker worker;

    public UserInfoServlet(DBWorker worker) {
        this.worker = worker;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        try {
            String token = req.getHeader("token");
            InfoResponse response;
            if (req.getParameterMap().containsKey("userId")) {
                Integer userId = Integer.parseInt(req.getParameter("userId"));
                response = new InfoWorker(worker).getInfo(userId, token);
            } else {
                response = new InfoWorker(worker).getInfo(token);
            }

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

