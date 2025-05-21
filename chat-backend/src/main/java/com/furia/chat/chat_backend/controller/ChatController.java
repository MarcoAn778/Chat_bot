package com.furia.chat.chat_backend.controller;

import com.furia.chat.chat_backend.external_api.FurIAApiService;
import com.furia.chat.chat_backend.model.Mensagem;
import com.furia.chat.chat_backend.repository.MensagemRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class ChatController {

    private final FurIAApiService furIAApiService;
    private final MensagemRepository mensagemRepository;

    public ChatController(MensagemRepository mensagemRepository, FurIAApiService furIAApiService) {
        this.mensagemRepository = mensagemRepository;
        this.furIAApiService = furIAApiService;
    }

    @MessageMapping("/enviar")
    @SendTo("/topico/publico")
    public Mensagem enviarMensagem(@Valid @Payload Mensagem mensagem, Principal principal) {
        // A mensagem já está validada aqui devido à anotação @Valid

        // Pega o nome do usuário autenticado diretamente do Principal
        String nomeUsuario = principal.getName();

        // Atribui o nome do usuário autenticado à mensagem
        mensagem.setRemetente(nomeUsuario);

        // Verifica se a mensagem é um comando especial
        if (mensagem.getConteudo().startsWith("!")) {
            return processarComando(mensagem);
        }

        // Salva a mensagem no banco
        mensagemRepository.save(mensagem);

        return mensagem;
    }

    // Método para processar comandos
    private Mensagem processarComando(Mensagem mensagem) {
        String conteudo = mensagem.getConteudo().trim();
        String[] partes = conteudo.split("\\s+", 2); // separa o comando e o argumento
        String comando = partes[0].toLowerCase();

        Mensagem resposta = new Mensagem();
        resposta.setRemetente("Bot");

        switch (comando) {
            case "!help":
                resposta.setConteudo("""
            Comandos disponíveis:
            !help - Lista os comandos
            !ping - Responde com 'Pong'
            !status - Mostra o status do chat
            !proximojogo - Mostra todos os próximos jogos
            !ultimosjogos - Mostra os últimos jogos
            !jogador <nome> - Mostra informações de um jogador
            !jogadores - Lista todos os jogadores
            !time <nome> - Mostra informações de um time e seus últimos e próximos jogos
            """);
                break;

            case "!ping":
                resposta.setConteudo("Pong");
                break;

            case "!status":
                long totalMensagens = mensagemRepository.count();
                resposta.setConteudo("Número total de mensagens no chat: " + totalMensagens);
                break;

            case "!proximojogo":
                resposta.setConteudo(furIAApiService.buscarProximosJogos());
                break;

            case "!ultimosjogos":
                resposta.setConteudo(furIAApiService.buscarUltimosJogos());
                break;

            case "!jogadores":
                resposta.setConteudo(furIAApiService.listarJogadoresProfissionais());
                break;

            case "!jogador":
                if (partes.length < 2) {
                    resposta.setConteudo("Uso correto: !jogador <nome>");
                } else {
                    String nomeJogador = partes[1].trim();
                    resposta.setConteudo(furIAApiService.buscarInfoJogadorPorNome(nomeJogador));
                }
                break;

            case "!time":
                if (partes.length < 2) {
                    resposta.setConteudo("Uso correto: !time <nome>");
                } else {
                    String nomeTime = partes[1].trim();
                    resposta.setConteudo(furIAApiService.buscarInfoTimePorNome(nomeTime));
                }
                break;

            default:
                resposta.setConteudo("Comando não reconhecido. Digite !help para ver os comandos disponíveis.");
                break;
        }

        return resposta;
    }


}
