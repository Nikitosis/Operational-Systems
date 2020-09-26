package com.client;

import com.client.service.SocketClient;
import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {
    @Value("${server.ip}")
    private String ip;

    @Value("${server.port}")
    private Integer port;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        SocketClient socketClient = new SocketClient(ip, port);
        socketClient.connect();
        CalcRequest calcRequest = socketClient.receiveMessage();

        CalcResponse calcResponse = CalcResponse.builder()
                .value(calcRequest.getValue() * 2)
                .build();

        socketClient.sendMessage(calcResponse);
    }
}