package com.smartventure.smartventure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentationDto(
        String id,
        String startup_id,
        String content_url,
        Double ai_score,
        Double plagiarism_score,
        OffsetDateTime processed_at
) { }