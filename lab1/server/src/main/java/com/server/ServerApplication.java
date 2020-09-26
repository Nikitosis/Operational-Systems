package com.server;

import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import com.server.service.SocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
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
                .value(12).waitingTime(Duration.ofSeconds(2)).build();
        CalcRequest calcRequest2 = CalcRequest.builder()
                .value(13).waitingTime(Duration.ofSeconds(2)).build();

        Future<CalcResponse> fResponse = socketServer.getSocketResponse(calcRequest1);
        Future<CalcResponse> gResponse = socketServer.getSocketResponse(calcRequest2);


        fResponse.get();
        gResponse.get();

    }
}
