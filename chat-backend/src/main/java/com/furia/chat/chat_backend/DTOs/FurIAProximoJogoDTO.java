package com.furia.chat.chat_backend.DTOs;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FurIAProximoJogoDTO {
    public String name;
    public String status;
    public String begin_at;

    public List<OpponentWrapper> opponents;
    public League league;
    public Serie serie;
    public Tournament tournament;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpponentWrapper {
        public Opponent opponent;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Opponent {
        public String name;
        public String location;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class League {
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Serie {
        public String season;
        public Integer year;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tournament {
        public String name;
        public String type;
    }
}

