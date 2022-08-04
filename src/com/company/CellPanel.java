package com.company;

import javax.swing.*;
import java.awt.*;

public class CellPanel extends JPanel {
    public int gridX;
    public int gridY;
    public int value;
    public CellPanel(int gridX,int gridY, int value, Color borderColor){
        this.gridX = gridX;
        this.gridY = gridY;
        this.value = value;
    }
}
