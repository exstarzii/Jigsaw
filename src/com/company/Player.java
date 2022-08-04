package com.company;

import java.awt.*;

public class Player{
    private int id;
    public String name;
    public Color figureColor;
    public int l;
    public int numberFigureSet;

    public Player(int id,String name, Color figureColor){
        this.id = id;
        this.name = name;
        this.figureColor = figureColor;
    }

    public int getId() {
        return id;
    }
}