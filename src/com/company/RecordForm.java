package com.company;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.io.*;

public class RecordForm extends JFrame implements ActionListener {
    private JFrame parent;
    private Client client;
    public RecordForm(JFrame parent, Client client) {
        this.client = client;
        this.parent = parent;
        String[] columns = new String[] {"Логин", "Дата игры", "количество ходов", "время игры"};
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5,5,5,5);
        JPanel panel1 = new JPanel(gridBagLayout,true);
        JButton button = new JButton("назад");
        button.addActionListener(this::actionPerformed);
        JLabel lab = new JLabel("Рекорды игроков");
        lab.setFont(new Font("SanSerif", Font.PLAIN, 21));



        gridBagLayout.columnWeights = new double[]{1.0f};
        gridBagLayout.rowWeights = new double[]{1.0f,10.0f,1.0f};

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 0;
        c.gridy = 0;
        panel1.add(lab,c);

        try {
            JTable table = new JTable(client.getScores(), columns);
            c.gridx = 0;
            c.gridy = 1;
            panel1.add(new JScrollPane(table), c);
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }

        c.gridx = 0;
        c.gridy = 2;
        panel1.add(button,c);


        panel1.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        setTitle("Jigsaw");
        setMinimumSize(new Dimension( 600,400));
        setLocationRelativeTo(null);
        setContentPane(panel1);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                parent.setVisible(true);
                parent.validate();
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        this.dispose();
    }

}
