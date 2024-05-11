package edu.hitsz.file;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ScoreDAO implements DAO{

    public static void addData(String username, int score, String date, int difficult) {
        if(difficult==0) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("score.txt", true))) {
                writer.println(username + " - Score: " + score + " - Date: " + date);
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        }
        else if(difficult==1) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("score1.txt", true))) {
                writer.println(username + " - Score: " + score + " - Date: " + date);
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        }
        else {
            try (PrintWriter writer = new PrintWriter(new FileWriter("score2.txt", true))) {
                writer.println(username + " - Score: " + score + " - Date: " + date);
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        }
    }

    public static void readData(List<String> leaderboard,int difficult) {
        if(difficult==0) {
            try (
                    BufferedReader reader = new BufferedReader(new FileReader("score.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    leaderboard.add(line);
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file.");
                e.printStackTrace();
            }
        }
        else if(difficult==1){
            try (
                    BufferedReader reader = new BufferedReader(new FileReader("score1.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    leaderboard.add(line);
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file.");
                e.printStackTrace();
            }
        }
        else{
            try (
                    BufferedReader reader = new BufferedReader(new FileReader("score2.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    leaderboard.add(line);
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file.");
                e.printStackTrace();
            }
        }
    }

    public static void sortData(List<String> leaderboard) {
        Collections.sort(leaderboard, new Comparator<String>() {

            public int compare(String entry1, String entry2) {
                int score1 = Integer.parseInt(entry1.split(" - Score: ")[1].split(" - Date: ")[0]);
                int score2 = Integer.parseInt(entry2.split(" - Score: ")[1].split(" - Date: ")[0]);
                return Integer.compare(score2, score1); // 降序排序
            }
        });
    }

    public static void deleteData( int difficulty,int lineNumberToDelete) throws IOException {
        List<String> leaderboard = new LinkedList<>();
        ScoreDAO.readData(leaderboard,difficulty);
        Collections.sort(leaderboard, new Comparator<String>() {

            public int compare(String entry1, String entry2) {
                int score1 = Integer.parseInt(entry1.split(" - Score: ")[1].split(" - Date: ")[0]);
                int score2 = Integer.parseInt(entry2.split(" - Score: ")[1].split(" - Date: ")[0]);
                return Integer.compare(score2, score1); // 降序排序
            }
        });
        if (lineNumberToDelete >= 0 && lineNumberToDelete < leaderboard.size()) {
            leaderboard.remove(lineNumberToDelete);
        } else {
            System.out.println("Line number out of range.");
        }

        BufferedWriter bufferedWriter = getBufferedWriter(leaderboard, difficulty);
        bufferedWriter.close();
        System.out.println("Line deleted successfully.");
        }

    private static BufferedWriter getBufferedWriter(List<String> leaderboard, int difficulty) throws IOException {
        String filePath;
        if(difficulty ==0){
            filePath = "score.txt";
        }
        else if (difficulty ==1) {
            filePath = "score1.txt";
        }
        else{
            filePath = "score2.txt";
        }
        File inputFile = new File(filePath);
        // 将剩余内容写入文件
        FileWriter fileWriter = new FileWriter(inputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (String outputLine : leaderboard) {
            bufferedWriter.write(outputLine + "\n");
        }
        return bufferedWriter;
    }
}


