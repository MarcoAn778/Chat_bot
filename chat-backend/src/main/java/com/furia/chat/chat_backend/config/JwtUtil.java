package com.furia.chat.chat_backend.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Deve ter pelo menos 32 caracteres (256 bits)
    private final String SEGREDO = "chave-super-segura-furia-chat-2025-0";
    private final SecretKey chaveSecreta = Keys.hmacShaKeyFor(SEGREDO.getBytes(StandardCharsets.UTF_8));

    public String gerarToken(String nomeUsuario) {
        return Jwts.builder()
                .setSubject(nomeUsuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                .signWith(chaveSecreta, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(chaveSecreta)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        String username = extrairUsername(token);
        return username.equals(userDetails.getUsername());
    }
}

