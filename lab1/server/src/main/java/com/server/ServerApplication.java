package com.server;

import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import com.server.service.SocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Calendar;
import java.util.concurrent.Future;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {
    private final SocketServer socketServer;

    @Autowired
    public ServerApplication(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CalcRequest calcRequest1 = CalcRequest.builder()
                .value(12).waitingSecs(1).build();
        CalcRequest calcRequest2 = CalcRequest.builder()
                .value(13).waitingSecs(2).build();

        Future<CalcResponse> responseFuture = socketServer.getSocketResponse(calcRequest1);

        responseFuture.get();

    }
}
