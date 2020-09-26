package com.server.configuration;

import com.server.service.SocketServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
public class Config {
    @Value("${port}")
    public Integer port;

    @Bean
    public ServerSocket serverSocket() throws IOException {
        return new ServerSocket(port);
    }
}
