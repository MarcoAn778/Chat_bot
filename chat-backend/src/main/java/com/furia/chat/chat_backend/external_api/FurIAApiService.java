package com.furia.chat.chat_backend.external_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furia.chat.chat_backend.DTOs.FurIAProximoJogoDTO;
import com.furia.chat.chat_backend.DTOs.JogadorDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class FurIAApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${pandascore.api.key}")
    private String apiKey;

    public String buscarProximosJogos() {
        // URL para buscar todos os próximos jogos
        String url = "https://api.pandascore.co/csgo/matches/upcoming?filter[videogame_title]=cs-2&per_page=15"; // Alterando para CS2 e limitando a 5 resultados

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            // Fazendo a requisição
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            FurIAProximoJogoDTO[] partidas = mapper.readValue(response.getBody(), FurIAProximoJogoDTO[].class);

            StringBuilder info = new StringBuilder();
            info.append("🎮 Próximos Jogos de Counter-Strike 2:\n");

            // Iterando para exibir os jogos
            for (FurIAProximoJogoDTO partida : partidas) {
                info.append("\n🏆 Torneio: ").append(partida.league.name)
                        .append(" - ").append(partida.tournament.name).append("\n");

                info.append("🆚 ");
                for (FurIAProximoJogoDTO.OpponentWrapper o : partida.opponents) {
                    info.append(o.opponent.name).append(" vs ");
                }
                // Remover o último "vs"
                info.setLength(info.length() - 4);

                info.append("\n📅 Data: ").append(partida.begin_at != null ? partida.begin_at.replace("T", " ").replace("Z", "") : "Indefinida")
                        .append("\n📡 Status: ").append(partida.status)
                        .append("\n============================\n");
            }

            if (info.length() > 0) {
                return info.toString(); // Exibe todos os jogos
            } else {
                return "Nenhum jogo futuro encontrado.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao obter dados dos próximos jogos.";
        }
    }
    public String buscarUltimosJogos() {
        // URL para buscar os últimos jogos
        String url = "https://api.pandascore.co/csgo/matches/past?filter[videogame_title]=cs-2&per_page=15"; // Limitando a 5 resultados

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            // Fazendo a requisição
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            FurIAProximoJogoDTO[] partidas = mapper.readValue(response.getBody(), FurIAProximoJogoDTO[].class);

            StringBuilder info = new StringBuilder();
            info.append("⚡ Últimos Jogos de Counter-Strike 2:\n");

            // Iterando para exibir os jogos
            for (FurIAProximoJogoDTO partida : partidas) {
                info.append("\n🏆 Torneio: ").append(partida.league.name)
                        .append(" - ").append(partida.tournament.name).append("\n");

                info.append("🆚 ");
                for (FurIAProximoJogoDTO.OpponentWrapper o : partida.opponents) {
                    info.append(o.opponent.name).append(" vs ");
                }
                // Remover o último "vs"
                info.setLength(info.length() - 4);

                info.append("\n📅 Data: ").append(partida.begin_at != null ? partida.begin_at.replace("T", " ").replace("Z", "") : "Indefinida")
                        .append("\n📡 Status: ").append(partida.status)
                        .append("\n============================\n");
            }

            if (info.length() > 0) {
                return info.toString(); // Exibe os últimos jogos
            } else {
                return "Nenhum jogo passado encontrado.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao obter dados dos últimos jogos.";
        }
    }

    public String listarJogadoresProfissionais() {
        String urlBase = "https://api.pandascore.co/csgo/players?page[size]=50&page[number]=";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        StringBuilder sb = new StringBuilder("🎮 Lista de jogadores profissionais:\n\n");
        int pagina = 1;
        boolean continuar = true;

        try {
            while (continuar) {
                String url = urlBase + pagina;

                ResponseEntity<JogadorDTO[]> resposta = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        JogadorDTO[].class
                );

                JogadorDTO[] jogadores = resposta.getBody();

                if (jogadores == null || jogadores.length == 0) {
                    continuar = false;
                } else {
                    for (JogadorDTO jogador : jogadores) {
                        sb.append("📛 ").append(jogador.name != null ? jogador.name : "Desconhecido");
                        sb.append(" - ").append(jogador.first_name != null ? jogador.first_name : "")
                                .append(" ").append(jogador.last_name != null ? jogador.last_name : "").append("\n");
                    }
                    pagina++;
                }
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao buscar jogadores.";
        }
    }

    public String buscarInfoJogadorPorNome(String nomeBuscado) {
        String urlBase = "https://api.pandascore.co/csgo/players?page[size]=50&page[number]=";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        int pagina = 1;
        boolean continuar = true;

        try {
            while (continuar) {
                String url = urlBase + pagina;

                ResponseEntity<JogadorDTO[]> resposta = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        JogadorDTO[].class
                );

                JogadorDTO[] jogadores = resposta.getBody();

                if (jogadores == null || jogadores.length == 0) {
                    continuar = false; // Encerra quando não houver mais jogadores
                } else {
                    for (JogadorDTO jogador : jogadores) {
                        if (jogador.name != null && jogador.name.equalsIgnoreCase(nomeBuscado)) {
                            // Encontrou o jogador, retorna os dados
                            return """
                        🧑 Jogador: %s %s
                        📛 Nick: %s
                        🧩 Função: %s
                        🏢 Time Atual: %s
                        🖼️ Imagem: %s
                        """.formatted(
                                    jogador.first_name != null ? jogador.first_name : "Desconhecido",
                                    jogador.last_name != null ? jogador.last_name : "Desconhecido",
                                    jogador.name != null ? jogador.name : "Desconhecido",
                                    jogador.role != null ? jogador.role : "Desconhecida",
                                    jogador.current_team != null && jogador.current_team.name != null ? jogador.current_team.name : "Sem time",
                                    jogador.image_url != null ? jogador.image_url : "Sem imagem"
                            );
                        }
                    }
                    pagina++; // Avança para a próxima página
                }
            }

            return "Nenhum jogador encontrado com esse nome.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao buscar informações do jogador.";
        }
    }

    public String buscarInfoTimePorNome(String nomeTime) {
        String urlBaseTimes = "https://api.pandascore.co/csgo/teams?page[size]=50&page[number]=";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            int pagina = 1;
            boolean continuar = true;
            JogadorDTO.Team timeEncontrado = null;

            // 1. Buscar todos os times paginando
            while (continuar) {
                String url = urlBaseTimes + pagina;
                ResponseEntity<JogadorDTO.Team[]> resposta = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        JogadorDTO.Team[].class
                );

                JogadorDTO.Team[] times = resposta.getBody();

                if (times == null || times.length == 0) break;

                for (JogadorDTO.Team time : times) {
                    if (time.name != null && time.name.toLowerCase().contains(nomeTime.toLowerCase())) {
                        timeEncontrado = time;
                        continuar = false;
                        break;
                    }
                }
                pagina++;
            }

            if (timeEncontrado == null) return "❌ Time não encontrado.";

            StringBuilder sb = new StringBuilder();
            sb.append("🏢 Time Encontrado: ").append(timeEncontrado.name).append("\n");

            // 2. Buscar os 3 últimos jogos do time
            String urlUltimos = "https://api.pandascore.co/csgo/matches/past?filter[opponent_id]=" + timeEncontrado.id + "&per_page=3";
            ResponseEntity<FurIAProximoJogoDTO[]> respUltimos = restTemplate.exchange(
                    urlUltimos,
                    HttpMethod.GET,
                    entity,
                    FurIAProximoJogoDTO[].class
            );

            sb.append("\n⚔️ Últimos Jogos:\n");
            for (FurIAProximoJogoDTO jogo : respUltimos.getBody()) {
                sb.append("🏆 ").append(jogo.league.name).append(" - ").append(jogo.tournament.name).append("\n");
                sb.append("🆚 ");
                for (FurIAProximoJogoDTO.OpponentWrapper o : jogo.opponents) {
                    sb.append(o.opponent.name).append(" vs ");
                }
                sb.setLength(sb.length() - 4);
                sb.append("\n📅 ").append(jogo.begin_at != null ? jogo.begin_at.replace("T", " ").replace("Z", "") : "Indefinida");
                sb.append("\n----------------------------\n");
            }

            // 3. Buscar próximo jogo
            String urlProximo = "https://api.pandascore.co/csgo/matches/upcoming?filter[opponent_id]=" + timeEncontrado.id + "&per_page=1";
            ResponseEntity<FurIAProximoJogoDTO[]> respProx = restTemplate.exchange(
                    urlProximo,
                    HttpMethod.GET,
                    entity,
                    FurIAProximoJogoDTO[].class
            );

            FurIAProximoJogoDTO[] proximo = respProx.getBody();
            if (proximo != null && proximo.length > 0) {
                FurIAProximoJogoDTO jogo = proximo[0];
                sb.append("\n⏭️ Próximo Jogo:\n");
                sb.append("🏆 ").append(jogo.league.name).append(" - ").append(jogo.tournament.name).append("\n");
                sb.append("🆚 ");
                for (FurIAProximoJogoDTO.OpponentWrapper o : jogo.opponents) {
                    sb.append(o.opponent.name).append(" vs ");
                }
                sb.setLength(sb.length() - 4);
                sb.append("\n📅 ").append(jogo.begin_at != null ? jogo.begin_at.replace("T", " ").replace("Z", "") : "Indefinida");
            } else {
                sb.append("\n⏭️ Nenhum jogo futuro encontrado.");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao buscar informações do time.";
        }
    }
}