package com.zhulery.responses;

import java.util.List;

public record SearchResponse(List<UserSearchResponse> results) {
}
