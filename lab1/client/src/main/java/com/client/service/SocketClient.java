package com.client.service;

import com.lab1.api.dto.CalcRequest;
import com.lab1.api.dto.CalcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketClient {
    private String ip;
    private Integer port;
    private Socket socket;

    public SocketClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        log.info("Connecting socket");
        socket = new Socket(ip, port);
    }

    public CalcRequest receiveMessage() throws IOException, ClassNotFoundException {
        log.info("Listening for message");
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        CalcRequest request = (CalcRequest) in.readObject();
        in.close();

        log.info("Message received={}", request);

        return request;
    }

    public void sendMessage(CalcResponse calcResponse) throws IOException {
        log.info("Sending message={}", calcResponse);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(calcResponse);
        out.close();
        log.info("Message sent");
    }
}
