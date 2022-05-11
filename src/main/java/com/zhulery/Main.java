package com.zhulery;

import com.zhulery.dbworkers.DBWorker;
import com.zhulery.servlets.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class Main {
    public static void main(String[] args) throws Exception {
        DBWorker worker = new DBWorker();

        AddRideServlet addRideServlet = new AddRideServlet(worker);
        FeedServlet feedServlet = new FeedServlet(worker);
        FollowServlet followServlet = new FollowServlet(worker);
        FollowsServlet followsServlet = new FollowsServlet(worker);
        RidesServlet ridesServlet = new RidesServlet(worker);
        RouteServlet routeServlet = new RouteServlet(worker);
        SearchServlet searchServlet = new SearchServlet(worker);
        SignInServlet signInServlet = new SignInServlet(worker);
        SignUpServlet signUpServlet = new SignUpServlet(worker);
        UnfollowServlet unfollowServlet = new UnfollowServlet(worker);
        UserInfoServlet userInfoServlet = new UserInfoServlet(worker);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(addRideServlet), "/addRide");
        context.addServlet(new ServletHolder(feedServlet), "/feed");
        context.addServlet(new ServletHolder(followServlet), "/follow");
        context.addServlet(new ServletHolder(followsServlet), "/follows");
        context.addServlet(new ServletHolder(signUpServlet), "/signUp");
        context.addServlet(new ServletHolder(ridesServlet), "/rides");
        context.addServlet(new ServletHolder(routeServlet), "/route");
        context.addServlet(new ServletHolder(searchServlet), "/search");
        context.addServlet(new ServletHolder(signInServlet), "/signIn");
        context.addServlet(new ServletHolder(unfollowServlet), "/unfollow");
        context.addServlet(new ServletHolder(userInfoServlet), "/info");


        Server server = new Server(80);
        server.setHandler(context);

        server.start();
        server.join();
    }
}
