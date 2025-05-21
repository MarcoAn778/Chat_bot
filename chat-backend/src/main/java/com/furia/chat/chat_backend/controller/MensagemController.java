package com.furia.chat.chat_backend.controller;

import com.furia.chat.chat_backend.model.Mensagem;
import com.furia.chat.chat_backend.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    @Autowired
    private MensagemRepository mensagemRepository;

    @GetMapping
    public List<Mensagem> listarUltimasMensagens() {
        List<Mensagem> mensagens = mensagemRepository.findTop50ByOrderByHorarioDesc();
        // Inverte a ordem para exibir da mais antiga para a mais recente
        Collections.reverse(mensagens);
        return mensagens;
    }
}
