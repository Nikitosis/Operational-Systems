package com.server;

import com.googlecode.lanterna.input.KeyType;
import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import com.lab1.api.dto.FuncType;
import com.server.service.ConsoleService;
import com.server.service.SocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.Future;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class ServerApplication implements CommandLineRunner {
    private final SocketServer socketServer;

    private final ConsoleService consoleService;

    @Value("${value}")
    private Integer value;

    @Value("${fPath}")
    private String fPath;

    @Value("${gPath}")
    private String gPath;

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ServerApplication.class);

        builder.headless(false);

        ConfigurableApplicationContext context = builder.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        CalcRequest calcRequest1 = CalcRequest.builder()
                .value(value)
                .build();
        CalcRequest calcRequest2 = CalcRequest.builder()
                .value(value)
                .build();

        ProcessBuilder processBuilderF = new ProcessBuilder("java", "-jar", fPath, "--server.ip=127.0.0.1", "--server.port=9001");
        Process funcF = processBuilderF.start();
        Socket fSocket = socketServer.connect();

        Thread.sleep(2000);

        ProcessBuilder processBuilderG = new ProcessBuilder("java", "-jar", gPath, "--server.ip=127.0.0.1", "--server.port=9001");
        Process funcG = processBuilderG.start();
        Socket gSocket = socketServer.connect();

        Future<CalcResponse> fResponseFuture = socketServer.getSocketResponse(fSocket, calcRequest1);

        Future<CalcResponse> gResponseFuture = socketServer.getSocketResponse(gSocket, calcRequest2);

        startExecution_SecondCancelation(fResponseFuture, gResponseFuture);
    }

    void startExecution_FirstCancelation(Future<CalcResponse> fResponseFuture,  Future<CalcResponse> gResponseFuture ) throws Exception{
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
                    consoleService.print("Result: " + fResponse.getValue()*gResponse.getValue());
                } else if (fResponse != null && fResponse.getValue().equals(0)) {
                    consoleService.print("F returned 0");
                } else if (gResponse != null && gResponse.getValue().equals(0)) {
                    consoleService.print("G returned 0");
                } else if (fResponse == null){
                    consoleService.print("Can't get result. F is not responding");
                } else {
                    consoleService.print("Can't get result. G is not responding");
                }
            } catch (Exception e) {}
        };

        consoleService.startListening(stopAction, KeyType.Escape);

        CalcResponse fResponse = null;
        CalcResponse gResponse = null;
        while(true) {
            if(fResponse == null && fResponseFuture.isDone()) {
                log.info("Received response from F");
                fResponse = fResponseFuture.get();
            }
            if(gResponse == null && gResponseFuture.isDone()) {
                log.info("Received response from G");
                gResponse = gResponseFuture.get();
            }
            if(fResponse != null && fResponse.getValue().equals(0)) {
                consoleService.print("Result is ready(F returned 0): " + 0);
                break;
            }
            if(gResponse != null && gResponse.getValue().equals(0)) {
                consoleService.print("Result is ready(G returned 0): " + 0);
                break;
            }

            if(fResponse != null && gResponse != null) {
                consoleService.print("Result is ready: " + fResponse.getValue() * gResponse.getValue());
                break;
            }
        }

        log.info("Finishing server execution");
    }

    void startExecution_SecondCancelation(Future<CalcResponse> fResponseFuture,  Future<CalcResponse> gResponseFuture ) throws Exception{
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
                    consoleService.print("Result: " + fResponse.getValue()*gResponse.getValue());
                } else if (fResponse != null && fResponse.getValue().equals(0)) {
                    consoleService.print("F returned 0");
                } else if (gResponse != null && gResponse.getValue().equals(0)) {
                    consoleService.print("G returned 0");
                } else if (fResponse == null){
                    consoleService.print("Can't get result. F is not responding");
                } else {
                    consoleService.print("Can't get result. G is not responding");
                }
                consoleService.stopAllListeners();
                consoleService.stopTimer();
            } catch (Exception e) {}
        };

        Runnable continueAction = () -> {
            consoleService.stopListening(KeyType.F1);
            consoleService.stopListening(KeyType.F2);
            consoleService.stopTimer();
            consoleService.clear();
        };

        Runnable escapeAction = () -> {
            try {
                consoleService.showPropmt();

                //on stop, stopAction
                consoleService.startListening(stopAction, KeyType.F1);
                //on continue, stop listening
                consoleService.startListening(continueAction, KeyType.F2);
                //15s timer
                consoleService.startTimer(stopAction, 15000L);
            } catch (Exception e) {}
        };

        consoleService.startListening(escapeAction, KeyType.Escape);

        CalcResponse fResponse = null;
        CalcResponse gResponse = null;
        while(true) {
            if(fResponse == null && fResponseFuture.isDone()) {
                log.info("Received response from F");
                fResponse = fResponseFuture.get();
            }
            if(gResponse == null && gResponseFuture.isDone()) {
                log.info("Received response from G");
                gResponse = gResponseFuture.get();
            }
            if(fResponse != null && fResponse.getValue().equals(0)) {
                consoleService.print("Result is ready(F returned 0): " + 0);
                break;
            }
            if(gResponse != null && gResponse.getValue().equals(0)) {
                consoleService.print("Result is ready(G returned 0): " + 0);
                break;
            }

            if(fResponse != null && gResponse != null) {
                consoleService.print("Result is ready: " + fResponse.getValue() * gResponse.getValue());
                break;
            }
        }

        consoleService.stopAllListeners();
        consoleService.stopTimer();

        log.info("Finishing server execution");
    }
}
