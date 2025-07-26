package com.smartventure.smartventure.dto;

import java.util.List;

public record InvestorInsert(
        String name,
        String email,
        List<Integer> subscribed_categories
) { }
