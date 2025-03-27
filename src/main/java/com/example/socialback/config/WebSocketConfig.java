package com.example.socialback.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // Client Subscribe ที่นี่
        config.setApplicationDestinationPrefixes("/app"); // Client ส่งข้อความมาที่นี่
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS(); // Frontend Connect ที่นี่
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptorAdapter);
    }
    @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    // เพิ่ม MappingJackson2MessageConverter
    messageConverters.add(new MappingJackson2MessageConverter());
    // false => ใช้คอนเวิร์ตเตอร์ default เพิ่มเติมได้
    return false;
  }
}
