package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameTable extends JPanel {
    protected CellPanel[][] cells;

    public Color cellColor =new Color(255,141,229);
    public  Color borderColor = new Color(142, 50, 131);
    public Color[] colorsOfPlayers;
    public Color[] borderColorsOfPlayers;

    public int rowCount=9;
    public int columnCount=9;
    public int borderThickness =2;

    public GameTable(int rowCount, int columnCount, int borderThickness,Color cellColor, Color borderColor,Color[] colorsOfPlayers,Color[] borderColorsOfPlayers) {
        setLayout(new GridBagLayout());
        this.colorsOfPlayers =colorsOfPlayers;
        this.borderColorsOfPlayers = borderColorsOfPlayers;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.borderThickness = borderThickness;
        this.cellColor = cellColor;
        this.borderColor = borderColor;
        setBackground(borderColor);
        SetTable();
    }
    public GameTable(Color[] colorsOfPlayers,Color[] borderColorsOfPlayers) {
        this.colorsOfPlayers =colorsOfPlayers;
        this.borderColorsOfPlayers = borderColorsOfPlayers;
        setLayout(new GridBagLayout());
        setBackground(borderColor);
        SetTable();
    }
    public void SetTable(){
        cells = new CellPanel[rowCount][columnCount];
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        //c.insets = new Insets(cellSpace, cellSpace, cellSpace, cellSpace);
        c.weightx=1;
        c.weighty=1;
        for(int i=0;i<rowCount;i++) {

            for (int j = 0; j < columnCount; j++) {
                // column 0
                c.gridx = i;
                // row 0
                c.gridy = j;
                CellPanel cell = new CellPanel(i, j, -1,borderColor);
                cells[i][j] = cell;
                this.add(cell, c);
            }
        }
        redrawGameTable();
    }

//    public void DrawFigure(CellPanel mainCell, Figure figure, Color colorPlayer){
//        int x;
//        int y;
//        for(Point cell : figure.relativePoint) {
//            x = mainCell.gridX + cell.x;
//            y = mainCell.gridY + cell.y;
//            if ((0 <= x && x < rowCount) && (0 <= y && y < columnCount)) {
//                //gameFields[x][y].value = 1;
//                cells[x][y].setBackground(colorPlayer);
//                cells[x][y].revalidate();
//            }
//        }
//    }

    public void redrawGameTable() {
        for (int x = 0; x < rowCount; x++) {
            for (int y = 0; y < columnCount; y++) {
                int playerId = cells[x][y].value;
                if(playerId ==-1){
                    cells[x][y].setBackground(cellColor);
                    cells[x][y].setBorder(BorderFactory.createLineBorder(borderColor, borderThickness));
                }else{
                    cells[x][y].setBackground(colorsOfPlayers[playerId%2]);
                    cells[x][y].setBorder(BorderFactory.createLineBorder(borderColorsOfPlayers[playerId%2], borderThickness));
                }
                cells[x][y].revalidate();
            }
        }
    }

    public void clearGameTable(){
        for (int x = 0; x < rowCount; x++) {
            for (int y = 0; y < columnCount; y++) {
                cells[x][y].value = -1;
                cells[x][y].setBackground(cellColor);
                cells[x][y].revalidate();
            }
        }
    }


    public boolean drawFigure(CellPanel mainCell, Figure figure, int playerId) {
        int x;
        int y;
        int successTries = 0;

        for (Point cell : figure.relativePoint) {
            x = mainCell.gridX + cell.x;
            y = mainCell.gridY + cell.y;
            if ((0 <= x && x <= 8) && (0 <= y && y <= 8) && cells[x][y].value == -1) {
                successTries++;
            }
        }

        if (successTries == figure.relativePoint.length) {
            for (Point cell : figure.relativePoint) {
                x = mainCell.gridX + cell.x;
                y = mainCell.gridY + cell.y;
                if ((0 <= x && x < rowCount) && (0 <= y && y < columnCount)) {
                    cells[x][y].value = playerId;
                }
            }
//            gameFields[x][y].setBackground(colorSet[1]);
//            gameFields[x][y].revalidate();
            redrawGameTable();
            return true;
        } else
            return false;
    }

}
