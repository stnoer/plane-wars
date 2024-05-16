package edu.hitsz.swing;

import edu.hitsz.application.*;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartMenu {
    private JButton simpleBtton;
    private JButton nolmalButton;
    private JButton difficultButton;
    private JComboBox musicComboBox;
    private JTextField musicTextField;
    private JPanel mainPanel;

    private boolean isPlay = true;

    private String selectedOption="开";


    public StartMenu() {
        simpleBtton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractGame game = new GameSimple(0,isPlay);
                Main.cardPanel.add(game);
                Main.cardLayout.last(Main.cardPanel);
                game.action();

            }
        });
        nolmalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractGame game = new GameNolmal(1,isPlay);
                Main.cardPanel.add(game);
                Main.cardLayout.last(Main.cardPanel);
                game.action();
            }
        });
        difficultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractGame game = new GameDifficult(2,isPlay);
                Main.cardPanel.add(game);
                Main.cardLayout.last(Main.cardPanel);
                game.action();
            }
        });
        musicComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedOption = (String) musicComboBox.getSelectedItem();
                isPlay= selectedOption.equalsIgnoreCase("开");

            }
        });
    }
    public JPanel getMainPanel() {
        return mainPanel;
    }
}
