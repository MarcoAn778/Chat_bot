package com.furia.chat.chat_backend.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JogadorDTO {
    public int id;
    public String name;
    public String first_name;
    public String last_name;
    public String role;
    public String image_url;
    public Team current_team;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Team {
        public String name;
        public int id;
    }
}

