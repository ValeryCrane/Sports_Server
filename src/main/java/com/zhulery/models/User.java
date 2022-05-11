package com.zhulery.models;

public record User(Integer id, String name, String email, Double height,
                   Double weight, String password, String token) {
}
