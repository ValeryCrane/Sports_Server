package com.zhulery.responses;

import com.google.gson.JsonElement;

public record RideResponse(Integer id, Integer userId, String username, String name,
                           String description, String sport, String datetime,
                           JsonElement statistics) {
}
