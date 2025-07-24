package com.smartventure.smartventure.service;


import org.springframework.web.reactive.function.client.WebClient;

public abstract class SupabaseService {
    protected final WebClient client;

    protected SupabaseService(WebClient supabaseClient) {
        this.client = supabaseClient;
    }
}