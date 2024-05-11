package edu.hitsz.swing;

import edu.hitsz.file.ScoreDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EndEasyMenu {
    private JPanel mainPanel;
    private JPanel top;
    private JPanel middle;
    private JPanel bottom;
    private JLabel easyText;
    private JScrollPane scoreScrollPanel;
    private JTable scoreTable;
    private JButton deleteButton;
    private JLabel titleTable;
    private int difficult;
    public String name;

    public EndEasyMenu(int difficult,int score,String date) {
        this.difficult=difficult;
        if(this.difficult==0) {
            easyText.setText("难度：EASY");
        }
        else if (this.difficult==1) {
            easyText.setText("难度：NOLMAL");
        }
        else{
            easyText.setText("难度：DIFFICULTY");
        }
        // 存读写新的记录信息
        name = JOptionPane.showInputDialog("请输入你的玩家名");
        ScoreDAO DAO = new ScoreDAO();
        ScoreDAO.addData(Objects.requireNonNullElse(name, "testUserName"), score, date, difficult);
        List<String> leaderboard = new LinkedList<>();
        ScoreDAO.readData(leaderboard,difficult);

        ScoreDAO.sortData(leaderboard);


        String[] columnName = {"排名","玩家","得分","记录时间"};
        String[][] tableData = new String[leaderboard.size()][4];

        for (int i = 0; i < leaderboard.size(); i++) {
            String[] parts = leaderboard.get(i).split(" - ");
            // 将分割后的部分存储到二维数组中
            tableData[i][0]=String.valueOf(i+1);

            tableData[i][1] = parts[0]; // 提取用户名部分
            tableData[i][2] = parts[1].split(": ")[1]; // 提取分数部分
            tableData[i][3] = parts[2].split(": ")[1]; // 提取日期部分
        }

        DefaultTableModel model = new DefaultTableModel(tableData, columnName){
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };
        //JTable 并不存储自己的数据，而是从表格模型那里获取它的数据
        scoreTable.setModel(model);
        scoreScrollPanel.setViewportView(scoreTable);


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = scoreTable.getSelectedRow();
                System.out.println(row);
                if(row != -1){
                    model.removeRow(row);
                }
                try {
                    ScoreDAO.deleteData(difficult,row);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
