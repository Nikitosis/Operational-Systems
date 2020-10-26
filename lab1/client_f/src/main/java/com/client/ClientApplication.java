package com.client;

import com.client.service.SocketClient;
import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import com.lab1.api.dto.FuncType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import spos.lab1.demo.DoubleOps;
import spos.lab1.demo.IntOps;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
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
        log.info("Starting");
        SocketClient socketClient = new SocketClient(ip, port);
        socketClient.connect();
        CalcRequest calcRequest = socketClient.receiveMessage();
        log.info("Received request={}", calcRequest);

        CalcResponse calcResponse = new CalcResponse();

        //calculate response regarding to function type
        calcResponse.setValue(funcF(calcRequest.getValue()));

        log.info("Sending response");
        socketClient.sendMessage(calcResponse);

        socketClient.close();
    }

    private Integer funcF(int value) {
        try {
            if (value == 0) {
                Thread.sleep(1000);
            } else if(value == 1) {
                Thread.sleep(3000);
            } else if(value == 2) {
                Thread.sleep(1000);
                return 0;
            } else if(value == 3) {
                wait();
            } else if(value == 4) {
                Thread.sleep(1000);
            } else if(value == 5) {
                wait();
            } else {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 3;
    }
}
