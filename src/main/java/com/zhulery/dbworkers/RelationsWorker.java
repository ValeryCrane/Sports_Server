package com.zhulery.dbworkers;

import com.zhulery.models.Relation;
import com.zhulery.models.User;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public record RelationsWorker(DBWorker worker) {

    public void follow(Integer userId, String token) throws SQLException, FileNotFoundException, IllegalAccessException {
        User follower = worker.getUser(token);
        User follows = worker.getUser(userId);
        worker.setRelation(follower, follows, Relation.followed);
    }

    public void unfollow(Integer userId, String token) throws SQLException, FileNotFoundException, IllegalAccessException {
        User follower = worker.getUser(token);
        User follows = worker.getUser(userId);
        worker.setRelation(follower, follows, Relation.unrelated);
    }
}
