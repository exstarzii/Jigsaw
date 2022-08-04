package com.company;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    public int maxGameTimeSec = 180;
    static List<Player> players = new ArrayList<Player>();
    public int lastId = 1;
    public String[] randomFigureLines = new String[50];
    public int maxPlayersCount=2;

    public Server(int port,int n, int maxGameTimeSec) throws IOException {
        this.maxGameTimeSec = maxGameTimeSec;
        maxPlayersCount = n;


        Random rand = new Random();
        int randomIndex;
        for (int i = 0; i < randomFigureLines.length; i++) {
            randomIndex = rand.nextInt(figures.length);
            randomFigureLines[i] = figures[randomIndex];
        }
        serverSocket = new ServerSocket(port);
        System.out.println("Server start");
        Runnable task = new Runnable() {
            public void run() {

                try {
                    start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < n; i++) {
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    public void start() throws IOException, InterruptedException {
        while (true) {
            System.out.println("wait for client");
            Socket clientSocket = serverSocket.accept();
            System.out.println("new client");
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            int lastFigureIndex = 0;
            mwhile:
            while (true) {
                String msg = in.readLine();
                if(msg==null){
                    continue;
                }
                System.out.println("get: " + msg);
                if (msg.length() > 13 && msg.startsWith("createPlayer;"))
                {
                    String playerName = msg.split(";")[1];
                    players.add(new Player(lastId, playerName, Color.ORANGE));
                    out.println(lastId);
                    lastId++;
                } else if (msg.equals("getPlayers"))
                {
                    String outMsg = "";
                    for (Player pl : players) {
                        outMsg += pl.getId() +","+pl.name+ ";";
                    }
                    out.println(outMsg.substring(0,outMsg.length()-1));
                }
                else if (msg.equals("waitPlayer"))
                {
//                    while (players.size() < maxPlayersCount) {
//                        Thread.currentThread().sleep(100);
//                    }
//                    out.println(maxGameTimeSec);
                    if(players.size() < maxPlayersCount){
                        out.println(-1);
                    }else{
                        out.println(maxGameTimeSec);
                    }
                }
                else if (msg.length() > 13 && msg.startsWith("waitGameEnd"))
                {

                    String[] msgSlpited = msg.split(";");
                    int id = Integer.parseInt(msgSlpited[1]);
                    int l = Integer.parseInt(msgSlpited[2]);
                    int numberFig = Integer.parseInt(msgSlpited[3]);
                    // сохраняем результаты игры
                    for (Player pl : players) {
                        if (pl.getId() == id) {
                            pl.l = l;
                            pl.numberFigureSet = numberFig;
                            DerbyAdapter.SaveScore(pl);
                            break;
                        }
                    }
                    //____________
                    // проверка все ли игроки закончили игру
                    // ждем оставшихся игроков
                    int count = 0;
                    for(Player pl : players){
                        if(pl.l != 0){
                            count++;
                        }
                    }
                    while (count!=players.size()) {
                        Thread.currentThread().sleep(100);
                        count =0;
                        for(Player pl : players){
                            if(pl.l != 0){
                                count++;
                            }
                        }
                    }
                    //_________
                    // получение результатов (проигрыш, выигрыш)
                    String ans = "win";
                    for(Player pl : players){
                        if(pl.numberFigureSet > numberFig || (pl.numberFigureSet == numberFig && pl.l < l)){
                            ans = "fail";
                        }
                    }
                    out.println(ans);
                }
                else if (msg.equals("getRandomFigure"))
                {
                    out.println(randomFigureLines[lastFigureIndex++]);
                }
                else if (msg.equals("checkAlive"))
                {
                    if(1==players.size()){
                        out.println(false);
                    }else{
                        out.println(true);
                    }
                }
                else if (msg.length() > 13 && msg.startsWith("removePlayer;"))
                {
                    String[] msgSlpited = msg.split(";");
                    int id = Integer.parseInt(msgSlpited[1]);
                    for (Player pl : players) {
                        if (pl.getId() == id) {
                            players.remove(pl);
                            break;
                        }
                    }
                    in.close();
                    out.close();
                    clientSocket.close();
                    break mwhile;

                }else if (msg.equals("getScores"))
                {
                    String[][] records;
                    try {
                       records = DerbyAdapter.getRecords();
                    }catch (Exception e){
                        out.println("error"+e.getMessage());
                        continue ;
                    }
                    String outMsg = "";
                    for (String[] row : records) {
                        outMsg += row[0] +","+row[1]+","+row[2]+","+row[3]+ ";";
                    }
                    out.println(outMsg.substring(0,outMsg.length()-1));

                } else {
                    out.println("unrecognised command");
                }
            }
        }
    }

    public void stopServer() throws IOException {
        serverSocket.close();
    }

    public static boolean checkHost(int port){
        try (Socket s = new Socket("localhost", port)) {
            s.close();
            return true;
        } catch (IOException ex) {
            /* ignore */
        }
        return false;
    }

    public String[] figures = {
            "0,0;0,1;0,2;1,0",
            "0,0;0,-1;1,0;2,0",
            "0,0;-1,0;0,-1;0,-2",
            "0,0;-2,0;-1,0;0,1",
            "0,0;-1,0;0,1;0,2",
            "0,0;-2,0;-1,0;0,-1",
            "0,0;0,-2;0,-1;1,0",
            "0,0;0,1;1,0;2,0",
            "0,0;-1,-1;-1,0;0,1",
            "0,0;-1,0;0,-1;1,-1",
            "0,0;0,1;1,0;1,-1",
            "0,0;-1,0;0,1;1,1",
            "0,0;-2,0;-1,0;0,-1;0,-2",
            "0,0;0,-2;0,-1;1,0;2,0",
            "0,0;0,2;0,1;1,0;2,0",
            "0,0;-2,0;-1,0;0,1;0,2",
            "0,0;-1,0;0,-2;0,-1;1,0",
            "0,0;-1,0;0,1;0,2;1,0",
            "0,0;0,-1;0,1;1,0;2,0",
            "0,0;-2,0;-1,0;0,-1;0,1",
            "0,0;-1,0;1,0",
            "0,0;0,-1;0,1",
            "0,0",
            "0,0;0,1;1,0",
            "0,0;-1,0;0,1",
            "0,0;-1,0;0,-1",
            "0,0;0,-1;1,0",
            "0,0;0,-1;1,0;0,1",
            "0,0;-1,0;0,1;1,0",
            "0,0;-1,0;0,-1;0,1",
            "0,0;-1,0;0,-1;1,0"
    };


}