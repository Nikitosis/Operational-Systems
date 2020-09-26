package com.client;

import com.client.service.SocketClient;
import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import com.lab1.api.dto.FuncType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import spos.lab1.demo.DoubleOps;
import spos.lab1.demo.IntOps;

import java.util.concurrent.TimeUnit;

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

        CalcResponse calcResponse = new CalcResponse();

        //calculate response regarding to function type
        if(FuncType.F.equals(calcRequest.getFuncType())) {
            calcResponse.setValue(IntOps.funcF(calcRequest.getValue()));
        } else {
            calcResponse.setValue(IntOps.funcG(calcRequest.getValue()));
        }

        Thread.sleep(calcRequest.getWaitingTime().toMillis());

        socketClient.sendMessage(calcResponse);

        socketClient.close();
    }
}
