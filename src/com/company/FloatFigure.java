package com.company;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class FloatFigure extends GameTable implements MouseListener, MouseMotionListener {
    private volatile int screenX = 0;
    private volatile int screenY = 0;
    private volatile int myX = 0;
    private volatile int myY = 0;
    private Point initialLocation;
    private GameTable origGameTable;
    private GameForm parent;


    public FloatFigure(GameForm gameForm,GameTable origGameTable, Color[] colorsOfPlayers,Color[] borderColorsOfPlayers){
        super(9,9,2,
                new Color(0,0,0, (float) 0.0),
                new Color(0,0,0, (float) 0.0),
                colorsOfPlayers,borderColorsOfPlayers);


        for (int x = 0; x < rowCount; x++) {
            for (int y = 0; y < columnCount; y++) {
                cells[x][y].addMouseListener(this);
                cells[x][y].addMouseMotionListener(this);
            }
        }
        this.origGameTable = origGameTable;
        this.parent = gameForm;
        initialLocation = getLocation();
    }

    public boolean drawFigure(Figure figure, int playerId) {
        return drawFigure(cells[4][4],figure,playerId);
    }


    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {

    }
    public void mousePressed(MouseEvent e) {
        CellPanel touchCell = (CellPanel) e.getSource();
        // проверка чтобы нельзя было перетаскивать за прозрачные части
        if (touchCell.value == -1) {
            return;
        }
        screenX = e.getXOnScreen();
        screenY = e.getYOnScreen();

        myX = getX();
        myY = getY();
    }
    public void mouseReleased(MouseEvent e){

        Point centerCellPos = cells[4][4].getLocationOnScreen();
        loop:
        for (int i = 0; i < origGameTable.rowCount; i++) {
            for (int j = 0; j < origGameTable.columnCount; j++) {
                Point cellPoint = origGameTable.cells[i][j].getLocationOnScreen();
                float dist = (float) origGameTable.cells[i][j].getHeight()/2;
                //if (origGameTable.cells[i][j].contains(dx, dy)) {
                if(centerCellPos.distance(cellPoint) < dist){
                    boolean canDraw = origGameTable.drawFigure(origGameTable.cells[i][j],parent.currentFigure,parent.currentPlayer.getId());
                    if(canDraw){
                        clearGameTable();
                        this.setLocation(initialLocation);
                        parent.onFigureSet();
                    }
                    System.out.println("x=" + i + "   y=" + j);
                    break loop;
                }
            }
        }


    }
    public void mouseClicked(MouseEvent e) {
    }


    public void mouseDragged(MouseEvent e) {
        CellPanel touchCell = (CellPanel) e.getSource();
        // проверка чтобы нельзя было перетаскивать за прозрачные части
        if (touchCell.value == -1) {
            return;
        }
        int deltaX = e.getXOnScreen() - screenX;
        int deltaY = e.getYOnScreen() - screenY;
        setLocation(myX + deltaX, myY + deltaY);
    }

    public void mouseMoved(MouseEvent e) { }

}
