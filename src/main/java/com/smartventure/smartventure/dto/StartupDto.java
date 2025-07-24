package com.smartventure.smartventure.dto;

import java.time.OffsetDateTime;

public record StartupDto(
        String id,
        String name,
        Integer category_id,
        OffsetDateTime created_at
) { }
