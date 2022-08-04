package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

public class GameForm extends JFrame implements ActionListener/*implements MouseListener */ {
    private JFrame parent;
    private JPanel panel1;
    private GameTable gameTable;
    private JLabel stateLabel;
    private JLabel maxTimeLabel;
    private JLabel playerNameLabel;
    private JLabel enemyNameLabel;
    private List<Player> allPlayers;
    private int numberOfFiguresSet =0;

    public Player currentPlayer;
    public Figure currentFigure;
    private FloatFigure floatFigure;
    public Color[] colorsOfPlayers = new Color[]{Color.GREEN, Color.BLUE, Color.ORANGE};
    public Color[] borderColorsOfPlayers = new Color[]{
            new Color(23, 87, 0),
            new Color(0, 29, 114),
            new Color(139, 108, 0)};
    private Client client;
    private int l=0;
    private java.util.Timer timer;
    private JButton button;
    private int maxGameTimeSec;
    private Thread threadWaitPlayer;
//    private Figure myFigure = new Figure(
//            new Point(0,0),new Point(0,-1), new Point(0,-2), new Point(-1,0));

    public GameForm(JFrame parent, Client client,String playerName) {
        this.client = client;
        this.parent = parent;

        stateLabel = new JLabel("Ожидание игрока");
        stateLabel.setFont(new Font("SanSerif", Font.PLAIN, 21));
        stateLabel.setBounds(10, 0, 500, 50);
        maxTimeLabel = new JLabel("");
        maxTimeLabel.setFont(new Font("SanSerif", Font.PLAIN, 21));
        maxTimeLabel.setBounds(10, 50, 500, 50);
        playerNameLabel = new JLabel("Я: "+playerName);
        playerNameLabel.setFont(new Font("SanSerif", Font.PLAIN, 21));
        playerNameLabel.setBounds(10, 350, 400, 50);
        enemyNameLabel = new JLabel("");
        enemyNameLabel.setFont(new Font("SanSerif", Font.PLAIN, 21));
        enemyNameLabel.setBounds(10, 400, 400, 50);
        button = new JButton("Завершить игру");
        button.addActionListener(this::actionPerformed);
        button.setBounds(300, 460, 150, 25);

        panel1 = new JPanel(null, true);
        panel1.setBounds(0, 0, 800, 550);
        gameTable = new GameTable(colorsOfPlayers, borderColorsOfPlayers);
        gameTable.setBounds(350, 50, 400, 400);
        floatFigure = new FloatFigure(this, gameTable, colorsOfPlayers, borderColorsOfPlayers);
        floatFigure.setBounds(0, 50, 400, 400);

        panel1.add(stateLabel);
        panel1.add(playerNameLabel);
        panel1.add(enemyNameLabel);
        panel1.add(maxTimeLabel);
        panel1.add(floatFigure);
        panel1.add(gameTable);
        panel1.add(button, 0);

        setTitle("Jigsaw");
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        setContentPane(panel1);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                threadWaitPlayer.interrupt();
                client.removePlayerAndClose(currentPlayer);
                parent.setVisible(true);
                parent.validate();
            }
        });
        validate();
        Runnable task = new Runnable() {
            public void run() {
                currentPlayer = client.createPlayer(playerName);
                maxGameTimeSec = client.waitPlayer();
                while(maxGameTimeSec == -1){
                    try {
                        Thread.currentThread().sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    maxGameTimeSec = client.waitPlayer();
                }
                allPlayers = client.getPlayers();
                currentFigure = client.getRandomFigure();
                gameStart();
            }
        };
        threadWaitPlayer = new Thread(task);
        threadWaitPlayer.start();
    }

    public void gameStart(){
        // проверка не прервал ли второй игрок сессию
        Runnable task = new Runnable() {
            public void run() {
                while(true){
                    try {
                        if(!client.checkAlive()){
                            actionPerformed(null);
                            break;
                        }
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        threadWaitPlayer = new Thread(task);
        threadWaitPlayer.start();
        for(Player pl : allPlayers){
            if(pl.getId() != currentPlayer.getId()){
                enemyNameLabel.setText("Противник: "+pl.name);
                break;
            }
        }
        maxTimeLabel.setText("Макс. время игры: "+maxGameTimeSec);
        floatFigure.drawFigure(currentFigure, currentPlayer.getId());
        floatFigure.setBounds(0,0,gameTable.getWidth(), gameTable.getHeight());
        timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            public int s = 0;
            public int m = 0;
            public int h = 0;

            public void run() {
                l++;
                h = l % (60 * 60 * 24) / (60 * 60);
                m = l % (60 * 60) / 60;
                s = l % 60;
                if(maxGameTimeSec <= l){
                    actionPerformed(null);
                }
                stateLabel.setText("Прошло " + h + " часов, " + m + " минут, " + s + " секунд");
            }
        }, 0, 1000);
    }


    public void onFigureSet() {
        currentFigure = client.getRandomFigure();
        floatFigure.drawFigure(currentFigure, currentPlayer.getId());
        numberOfFiguresSet++;
    }

    public void actionPerformed(ActionEvent e) {
        threadWaitPlayer.interrupt();
        if(l==0){
            JOptionPane.showMessageDialog(this,
                    "Игра отменена",
                    "Завершение игры",
                    JOptionPane.INFORMATION_MESSAGE);
            client.removePlayerAndClose(currentPlayer);
            parent.setVisible(true);
            parent.validate();
            this.dispose();
        }else {
            if(maxGameTimeSec<=l){
                stateLabel.setText("Игра окончена по лимиту времени.Ожидание окончания игры вторым игроком");
            }
            else {
                stateLabel.setText("Ожидание окончания игры вторым игроком");
            }
            button.setVisible(false);
            timer.cancel();
            timer.purge();
            Runnable task = new Runnable() {
                public void run() {
                    String res = client.waitGameEnd(currentPlayer, l, numberOfFiguresSet);
                    showGameEnd(res,false);
                }
            };
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    public void showGameEnd(String res, boolean byTime){
        if(res.equals("win")){
            res = "Вы выиграли. ";
        }else{
            res = "Вы проиграли. ";
        }
        int h = l % (60 * 60 * 24) / (60 * 60);
        int m = l % (60 * 60) / 60;
        int s = l % 60;
        String timeText = "Прошло " + h + " часов, " + m + " минут, " + s + " секунд";
        String msgText = res + timeText + ". Сделано " + numberOfFiguresSet + " ходов";
        JOptionPane.showMessageDialog(this,
                msgText,
                "Завершение игры",
                JOptionPane.INFORMATION_MESSAGE);
        parent.setVisible(true);
        parent.validate();
        client.removePlayerAndClose(currentPlayer);
        this.dispose();
    }
}