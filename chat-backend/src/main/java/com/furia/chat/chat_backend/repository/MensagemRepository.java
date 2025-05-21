package com.furia.chat.chat_backend.repository;

import com.furia.chat.chat_backend.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findTop50ByOrderByHorarioDesc(); // Pega as 50 últimas mensagens
}
