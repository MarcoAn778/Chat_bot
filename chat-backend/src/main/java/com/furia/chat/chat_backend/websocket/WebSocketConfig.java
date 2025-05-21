package com.furia.chat.chat_backend.websocket;

import com.furia.chat.chat_backend.config.AutenticacaoChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefixo dos tópicos onde os clientes se inscrevem para receber mensagens
        config.enableSimpleBroker("/topico");
        // Prefixo dos endpoints usados pelos clientes para enviar mensagens
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conexão websocket com fallback para SockJS
        registry.addEndpoint("/chat-furia").setAllowedOriginPatterns("*").withSockJS();
    }
    @Autowired
    private AutenticacaoChannelInterceptor autenticacaoChannelInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(autenticacaoChannelInterceptor);
    }
}
