package com.company;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class GameMenu extends JFrame implements ActionListener {

    public String playerName = "Player1";
    private Server server;
    private Client client;
    public GameMenu() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        JPanel panel1 = new JPanel(gridBagLayout, true);
        JLabel label = new JLabel("Имя игрока: ");
        JTextField textField = new JTextField(playerName + "            ");
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                playerName = textField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                playerName = textField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                playerName = textField.getText();
            }
        });
        JButton button1 = new JButton("Рекорды");
        button1.addActionListener(this::OpenRecordForm);
        JButton button2 = new JButton("Сетевая игра");
        button2.addActionListener(this::netGame);
        JButton button3 = new JButton("Выйти");
        button3.addActionListener(this::closeGame);
        gridBagLayout.columnWeights = new double[]{1.0f, 1.0f};
        gridBagLayout.rowWeights = new double[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

        c.gridx = 0;
        c.gridy = 0;
        panel1.add(label, c);

        c.gridx = 1;
        c.gridy = 0;
        panel1.add(textField, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 0;
        c.gridy = 1;
        panel1.add(button1, c);

        c.gridy = 2;
        panel1.add(button2, c);

        c.gridy = 3;
        panel1.add(button3, c);

        panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setTitle("Jigsaw");
        setMinimumSize(new Dimension(250, 300));
        setLocationRelativeTo(null);
        setContentPane(panel1);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
                /* code run when component hidden*/
            }

            public void componentShown(ComponentEvent e) {
                try {
                    if (client != null) {
                        client.stopConnection();
                        client = null;
                    }
                    if (server != null) {
                        server.stopServer();
                        server = null;
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }


    public void netGame(ActionEvent e) {
        setVisible(false);
        Client client = new Client();
        try {
                    try {
                        // если хоста нет, создаем
                        if (!client.startConnection("localhost", 5000)) {
                            Server server = new Server(5000, 2, 180);
                            client.startConnection("localhost", 5000);
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }



            GameForm form = new GameForm(this, client, playerName);
        }catch (Exception er) {
            JOptionPane.showMessageDialog(null,
                    er.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void closeGame(ActionEvent e) {
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
    public void OpenRecordForm(ActionEvent e) {
        setVisible(false);
        Client client = new Client();
        try {
            // если хоста нет, создаем
            if (!client.startConnection("localhost", 5000)) {
                Server server = new Server(5000, 2, 180);
                client.startConnection("localhost", 5000);
            }
            RecordForm form = new RecordForm(this, client);
        } catch (Exception er) {
            JOptionPane.showMessageDialog(this,
                    er.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
