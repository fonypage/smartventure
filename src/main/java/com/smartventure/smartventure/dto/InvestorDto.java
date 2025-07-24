package com.smartventure.smartventure.dto;

import java.util.List;

public record InvestorDto(
        String id,
        String name,
        String email,
        List<Integer> subscribed_categories
) { }
