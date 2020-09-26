package com.server;

import com.googlecode.lanterna.input.KeyType;
import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import com.lab1.api.dto.FuncType;
import com.server.service.ConsoleService;
import com.server.service.SocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.util.concurrent.Future;

@SpringBootApplication
@Slf4j
public class ServerApplication implements CommandLineRunner {
    private final SocketServer socketServer;

    private ConsoleService consoleService;

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
        consoleService = new ConsoleService();
        consoleService.print("Press enter if you are ready to start");
        consoleService.startListening(() -> {
            try {
                consoleService.clear();
                consoleService.stopListening(KeyType.Enter);
                startExecution_SecondCancelation();
            } catch (Exception e) {}
        }, KeyType.Enter);
    }

    void startExecution_FirstCancelation() throws Exception{
        CalcRequest calcRequest1 = CalcRequest.builder()
                .value(5)
                .funcType(FuncType.F)
                .build();
        CalcRequest calcRequest2 = CalcRequest.builder()
                .value(5)
                .funcType(FuncType.G)
                .build();

        Future<CalcResponse> fResponseFuture = socketServer.getSocketResponse(calcRequest1);
        Future<CalcResponse> gResponseFuture = socketServer.getSocketResponse(calcRequest2);

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

    void startExecution_SecondCancelation() throws Exception{
        CalcRequest calcRequest1 = CalcRequest.builder()
                .value(5)
                .funcType(FuncType.F)
                .build();
        CalcRequest calcRequest2 = CalcRequest.builder()
                .value(5)
                .funcType(FuncType.G)
                .build();

        Future<CalcResponse> fResponseFuture = socketServer.getSocketResponse(calcRequest1);
        Future<CalcResponse> gResponseFuture = socketServer.getSocketResponse(calcRequest2);

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

        Runnable escapeAction = () -> {
            try {
                consoleService.showPropmt();

                //on stop, stopAction
                consoleService.startListening(stopAction, KeyType.F1);
                //on continue, stop listening
                consoleService.startListening(() -> {
                    consoleService.stopListening(KeyType.F1);
                    consoleService.stopListening(KeyType.F2);
                    consoleService.stopTimer();
                    consoleService.clear();
                }, KeyType.F2);
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
