package com.server.service;

import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class SocketServer {
    private final ServerSocket serverSocket;

    private Socket fSocket;
    private Socket gSocket;
    private ExecutorService executorService;

    public SocketServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        executorService = Executors.newFixedThreadPool(2);
    }

    public Future<CalcResponse> getSocketResponse(CalcRequest calcRequest) throws IOException {
        return executorService.submit(() -> {
            log.info("Trying to obtain connection with socket");
            Socket fSocket = serverSocket.accept();
            log.info("Connection with socket obtained");

            log.info("Sending message={}", calcRequest);
            CalcResponse response = requestSocket(fSocket, calcRequest);
            log.info("Received response={}", response);

            fSocket.close();

            return response;
        });
    }

    private CalcResponse requestSocket(Socket socket, CalcRequest calcRequest) throws IOException, ClassNotFoundException {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(calcRequest);
            CalcResponse response = (CalcResponse) in.readObject();

            out.close();
            in.close();

            return response;
    }
}
