package com.zhulery.dbworkers;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public record SignWorker(DBWorker worker) {

    public String signUp(String name, String email, Double height, Double weight, String password)
            throws SQLException, IllegalAccessException, FileNotFoundException {
        return worker.addUser(name, email, height, weight, password).token();
    }

    public String signIn(String email, String password) throws SQLException,
            FileNotFoundException, IllegalAccessException {
        return worker.getUser(email, password).token();
    }
}
