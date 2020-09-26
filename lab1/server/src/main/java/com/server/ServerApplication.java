package com.server;

import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import com.lab1.api.dto.FuncType;
import com.server.service.ConsoleService;
import com.server.service.SocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import spos.lab1.demo.IntOps;

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
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ServerApplication.class);

        builder.headless(false);

        ConfigurableApplicationContext context = builder.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        CalcRequest calcRequest1 = CalcRequest.builder()
                .value(12)
                .waitingTime(Duration.ofSeconds(2))
                .funcType(FuncType.F)
                .build();
        CalcRequest calcRequest2 = CalcRequest.builder()
                .value(13)
                .waitingTime(Duration.ofSeconds(2))
                .funcType(FuncType.G)
                .build();


        Future<CalcResponse> fResponseFuture = socketServer.getSocketResponse(calcRequest1);
        Future<CalcResponse> gResponseFuture = socketServer.getSocketResponse(calcRequest2);


        ConsoleService consoleService = new ConsoleService();

        Runnable stopAction = () -> {
            try {
                CalcResponse fResponse = null;
                CalcResponse gResponse = null;
                if (fResponseFuture.isDone()) {
                    fResponse = fResponseFuture.get();
                }
                if (gResponseFuture.isDone()) {
                    gResponse = gResponseFuture.get();
                }
                if (fResponse != null && gResponse != null) {
                    consoleService.print("OK");
                } else if (fResponse != null && fResponse.getValue().equals(0)) {
                    consoleService.print("0");
                } else if (gResponse != null && gResponse.getValue().equals(0)) {
                    consoleService.print("0");
                } else {
                    consoleService.print("Can't get result");
                }
            } catch (Exception e) {
            }
        };

        consoleService.start(stopAction);

        fResponseFuture.get();
        gResponseFuture.get();

    }
}
