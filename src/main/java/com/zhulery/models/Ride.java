package com.zhulery.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public record Ride(Integer id, String datetime, String name, String description,
                   String sport, Integer userId, JsonElement statistics, JsonArray route) {
}

