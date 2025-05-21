package com.furia.chat.chat_backend.controller;

import com.furia.chat.chat_backend.DTOs.LoginDTO;
import com.furia.chat.chat_backend.config.JwtUtil;
import com.furia.chat.chat_backend.model.Usuario;
import com.furia.chat.chat_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getNomeUsuario(), login.getSenha())
            );

            String token = jwtUtil.gerarToken(login.getNomeUsuario());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody LoginDTO login) {
        if (usuarioRepo.findByNomeUsuario(login.getNomeUsuario()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já existe");
        }
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(login.getNomeUsuario());
        usuario.setSenha(new BCryptPasswordEncoder().encode(login.getSenha()));
        usuarioRepo.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

