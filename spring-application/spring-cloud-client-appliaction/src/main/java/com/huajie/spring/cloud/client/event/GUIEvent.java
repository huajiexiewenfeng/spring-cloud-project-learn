package com.huajie.spring.cloud.client.event;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;

public class GUIEvent {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("GUI程序-java 事件监听机制");

        jFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.printf("[%s]事件：%s\n", Thread.currentThread().getName(), e);
            }
        });

        jFrame.setBounds(300, 300, 400, 300);
        jFrame.setVisible(true);

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                jFrame.dispose();
            }
        });

    }
}
