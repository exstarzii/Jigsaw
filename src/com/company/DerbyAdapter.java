package com.company;
import javax.swing.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DerbyAdapter {
    static String urlConnection = "jdbc:derby:baeldung;";

    public static String[][] getRecords() throws SQLException {
        List<String[]> rows = new ArrayList<String[]>();
//        String[][] rows = new String[10][];

        ResultSet res = GetDataByQuery("SELECT * FROM score ORDER BY score_date DESC, score_time DESC, score_count DESC fetch first 10 rows only");

        int i = 0;
        while (res.next()) {
            rows.add(new String[]{
                    res.getString("score_login"),
                    res.getString("score_date"),
                    res.getString("score_count"),
                    res.getString("score_time")
            });
        }

        return rows.toArray(new String[0][]);
//                System.out.println(res.getInt("score_id")+" "+
//                res.getString("score_login")+" "+res.getTimestamp("score_date")+
//                " "+ res.getInt("score_count")+" "+res.getInt("score_time"));
//                }
    }
    public static  void SaveScore(Player pl){
        try {
            DerbyAdapter.MakeQuery(String.format("INSERT INTO score(score_login, score_date, score_count, score_time) VALUES ('%s',CURRENT_TIMESTAMP, %d, %d)",
                    pl.name, pl.numberFigureSet, pl.l));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void MakeQuery(String sql) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection con = DriverManager.getConnection(urlConnection);
        Statement statement = con.createStatement();
        statement.execute(sql);
    }

    public static ResultSet GetDataByQuery(String sql) throws SQLException {
        Connection con = DriverManager.getConnection(urlConnection);
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery(sql);
        return result;
    }
}
//    DriverManager.getConnection("jdbc:derby:;shutdown=true");

//        DerbyAdapter.MakeQuery("DROP TABLE score");
//        DerbyAdapter.MakeQuery("CREATE TABLE score (" +
//                "score_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1),
//                score_login varchar(255), score_date TIMESTAMP, score_count int, score_time int)");
//        DerbyAdapter.MakeQuery("INSERT INTO score VALUES (1, 'RandomLogin',CURRENT_TIMESTAMP, 6, 100)," +
//                "(2, 'RandomLogin',CURRENT_TIMESTAMP, 7, 100)," +
//                "(3, 'RandomLogin',CURRENT_TIMESTAMP, 8, 100)," +
//                "(4, 'RandomLogin2',CURRENT_TIMESTAMP, 6, 100)," +
//                "(5, 'RandomLogin2',CURRENT_TIMESTAMP, 7, 100)," +
//                "(6, 'RandomLogin2',CURRENT_TIMESTAMP, 8, 100)," +
//                "(7, 'RandomLogin',CURRENT_TIMESTAMP, 8, 109)");
//ResultSet res = DerbyAdapter.GetDataByQuery("SELECT * FROM score");
//        while(res.next()){
//                System.out.println(res.getInt("score_id")+" "+
//                res.getString("score_login")+" "+res.getTimestamp("score_date")+
//                " "+ res.getInt("score_count")+" "+res.getInt("score_time"));
//                }