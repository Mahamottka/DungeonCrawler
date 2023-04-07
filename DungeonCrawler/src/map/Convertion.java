package map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts data from .txt or .tmj/.json
 * fromTxt method returns list of ID´s from particular .txt file
 * ID1: hallway
 * ID2: doorway
 * ID3: wall
 */
public class Convertion {

    private final List<Integer> idList = new ArrayList<>();
    int[][] cells = new int[30][30];


    public Convertion() {
        fromTxt();
    }

    private void fromTxt() {
        String filePath = "Coordinates.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] numbersArray = line.split(",");
                for (String num : numbersArray) {
                    idList.add(Integer.parseInt(num.trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public int[][] getIdArray() {
        int index = 0;
        if (cells != null) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    cells[i][j] = idList.get(index++);
                }
            }
        }
        return cells;
    }
}
