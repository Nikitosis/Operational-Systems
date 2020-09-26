package com.server.service;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import jline.console.ConsoleReader;
import jline.console.KeyMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import static com.googlecode.lanterna.gui2.table.TableCellBorderStyle.EmptySpace;

@Slf4j
public class ConsoleService {
    private Terminal terminal;
    private Screen screen;
    private boolean updatable = true;

    public ConsoleService() throws IOException {
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
    }

    public void start(Runnable stopAction) throws IOException {
        while(true && updatable) {
            if(KeyType.Escape.equals(terminal.readInput().getKeyType())) {
                log.info("Escape is pressed, calling stopAction");
                stopAction.run();
                updatable = false;
            }
        }
    }

    public void print(String msg) throws IOException {
        screen.newTextGraphics().putString(1,1, msg);
        screen.refresh();
    }

}
