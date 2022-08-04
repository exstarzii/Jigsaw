package com.company;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public boolean startConnection(String ip, int port) throws IOException {
        try {
            clientSocket = new Socket(ip, port);
        }
        catch (Exception e){
            return false;
        }
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        return true;
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public Player createPlayer(String playerName){
        String answer = null;
        try {
            answer = sendMessage("createPlayer;"+playerName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(answer);
        return new Player(Integer.parseInt(answer),"Player", Color.GREEN);
    }
    public int waitPlayer(){
        String answer = null;
        try {
            answer = sendMessage("waitPlayer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(answer);
    }
    public String waitGameEnd(Player player,int l,int numberOfFiguresSet){
        String answer = null;
        try {
            answer = sendMessage("waitGameEnd;"+player.getId()+";"+l+";"+numberOfFiguresSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public List<Player> getPlayers(){
        String answer = null;
        try {
            answer = sendMessage("getPlayers");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Player> players = new ArrayList<>();
        String[] str = answer.split(";");
        for(int i=0;i<str.length;i++){
            String[] substr = str[i].split(",");
            players.add(new Player(Integer.parseInt(substr[0]),substr[1],Color.GREEN));
        }
        System.out.println(answer);
        return players;
    }

    public Figure getRandomFigure() {
        String answer = null;
        try {
            answer = sendMessage("getRandomFigure");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return MyParser.GetFigure(answer);
    }
    public void removePlayerAndClose(Player player){
        String answer = null;
        try {
            answer = sendMessage("removePlayer;"+player.getId());
            stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String[][] getScores() throws Exception {
        String answer = null;
        try {
            answer = sendMessage("getScores");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(answer.startsWith("error")){
            throw new Exception(answer);
        }

        String[] str = answer.split(";");
        String[][] res = new String[str.length][];
        for(int i=0;i<str.length;i++){
            String[] substr = str[i].split(",");
            res[i] = substr;
        }
        System.out.println(answer);
        return res;
    }

    public boolean checkAlive() {
        String answer = null;
        try {
            answer = sendMessage("checkAlive");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.parseBoolean(answer);
    }
}
