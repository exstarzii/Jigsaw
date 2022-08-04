package com.company;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MyParser {
    public static List<Figure> GetData(String path){
        List<Figure> figuresList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            //чтение построчно
            String s;
            while((s=br.readLine())!=null){
                String[] pointsString = s.split(";");
                Point[] points = new Point[pointsString.length];
                for(int i=0;i<pointsString.length;i++){
                    int x = Integer.parseInt(pointsString[i].split(",")[0]);
                    int y = Integer.parseInt(pointsString[i].split(",")[1]);
                    points[i] = new Point(x,y);
                }
                figuresList.add(new Figure(points));
                System.out.println(s);
            }

        }

        catch (Exception e){
            //System.out.println("Working Directory = " + System.getProperty("user.dir"));
            System.out.println(e.getMessage());
        }
        return figuresList;
    }
    public static Figure GetFigure(String input){
        String[] pointsString = input.split(";");
        Point[] points = new Point[pointsString.length];
        for(int i=0;i<pointsString.length;i++){
            int x = Integer.parseInt(pointsString[i].split(",")[0]);
            int y = Integer.parseInt(pointsString[i].split(",")[1]);
            points[i] = new Point(x,y);
        }
        return new Figure(points);
    }
}
