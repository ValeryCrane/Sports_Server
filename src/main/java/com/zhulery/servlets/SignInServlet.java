package com.zhulery.servlets;

import com.google.gson.Gson;
import com.zhulery.dbworkers.DBWorker;
import com.zhulery.dbworkers.SignWorker;
import com.zhulery.requests.SignInRequest;
import com.zhulery.responses.SignResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SignInServlet extends HttpServlet {
    private final DBWorker worker;

    public SignInServlet(DBWorker worker) {
        this.worker = worker;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();

        try {
            SignInRequest signInRequest = gson.fromJson(req.getReader(), SignInRequest.class);
            SignResponse jsonResponse = new SignResponse(
                    new SignWorker(worker).signIn(signInRequest.email, signInRequest.password)
            );
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            gson.toJson(jsonResponse, resp.getWriter());
        } catch (Exception exception) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("text/plain");
            resp.getWriter().println(exception.getMessage());
        }
    }
}
