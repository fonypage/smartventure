package com.smartventure.smartventure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StartupInsert(
        @JsonProperty("name") String name,
        @JsonProperty("category_id") Integer categoryId
) {}

