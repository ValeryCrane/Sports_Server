package com.zhulery.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AddRideRequest {
    public String name;
    public String description;
    public String sport;
    public JsonObject statistics;
    public JsonArray route;
}
