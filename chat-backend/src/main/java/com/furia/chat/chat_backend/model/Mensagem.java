package com.furia.chat.chat_backend.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O remetente é obrigatório")
    private String remetente;

    @NotBlank(message = "O conteúdo da mensagem não pode estar vazio")
    @Size(max = 500, message = "A mensagem não pode exceder 500 caracteres")
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime horario;


    @PrePersist
    protected void onCreate() {
        this.horario = LocalDateTime.now();
    }

    // Construtores
    public Mensagem() {}

    public Mensagem(String remetente, String conteudo, LocalDateTime horario) {
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.horario = horario;
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRemetente() { return remetente; }
    public void setRemetente(String remetente) { this.remetente = remetente; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getHorario() { return horario; }
    public void setHorario(LocalDateTime horario) { this.horario = horario; }
}
