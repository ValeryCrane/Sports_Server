package com.zhulery.responses;

import com.google.gson.JsonElement;

public record InfoResponse(Integer id, String name, String relation, String email,
                           Double height, Double weight, JsonElement statistics) {
}
