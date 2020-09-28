package com.server.service;

import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConsoleService {
    private Terminal terminal;
    private Screen screen;
    private Map<KeyType, Runnable> keyActions = new HashMap<>();
    private Thread keyListener;
    private Thread timerThread;

    public ConsoleService() throws IOException {
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        keyListener= new Thread(() -> {
            try {
                listenKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        keyListener.start();
    }

    public void stopAllListeners() {
        keyActions.clear();
    }

    public void stopListening(KeyType keyType) {
        log.info("Stopping listening {}", keyType);
        keyActions.remove(keyType);
    }

    public void stopTimer() {
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
            log.info("Thread interrupted");
        }
    }

    public void startListening(Runnable action, KeyType keyType) throws IOException {
        log.info("Starting listening for key press {}", keyType);
        keyActions.put(keyType, action);
    }

    public void startTimer(Runnable action, Long millsAm) {
        log.info("Starting timer {}", millsAm);
        timerThread = new Thread(() -> {
            try {
                Object monitor = new Object();
                synchronized (monitor) {
                    monitor.wait(millsAm);
                }
                //after timer passed, action is run
                log.info("Timer clocks");
                action.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timerThread.start();
    }

    private void listenKey() throws IOException, InterruptedException {
        while (true) {
            KeyType pressedKey = terminal.readInput().getKeyType();
            if(pressedKey == KeyType.EOF) {
                break;
            }
            log.info("Key pressed {}", pressedKey);
            if (keyActions.containsKey(pressedKey)) {
                log.info("Handling {} listener", pressedKey);
                new Thread(() -> keyActions.get(pressedKey).run()).start();
            }
        }
    }

    public void showPropmt() throws IOException {
        screen.clear();
        screen.newTextGraphics().putString(1,1, "(F1) stop");
        screen.newTextGraphics().putString(1,2, "(F2) continue");
        screen.refresh();
    }

    public void clear() {
        screen.clear();
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(String msg) throws IOException {
        screen.clear();
        screen.newTextGraphics().putString(1,1, msg);
        screen.refresh();
    }

}
