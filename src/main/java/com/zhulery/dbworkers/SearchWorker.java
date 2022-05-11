package com.zhulery.dbworkers;

import com.zhulery.models.User;
import com.zhulery.responses.SearchResponse;
import com.zhulery.responses.UserSearchResponse;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record SearchWorker(DBWorker worker) {

    public SearchResponse request(String query) throws SQLException, FileNotFoundException {
        List<User> users = worker.getUsers(query);
        List<UserSearchResponse> response = new ArrayList<>();
        for (User user: users) {
            response.add(new UserSearchResponse(
                    user.id(),
                    user.name()
            ));
        }
        return new SearchResponse(response);
    }
}
