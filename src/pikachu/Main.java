package pikachu;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static ArrayList<String> readFileByLines(String fileName) {
        ArrayList<String> newArrayList = new ArrayList<>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                // System.out.println("line " + line + ": " + tempString);
                newArrayList.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return newArrayList;
    }

    public static void main(String[] args) {
        // Load files from the raw corpus
        ArrayList<String> commentFile = readFileByLines(args[0]);
        ArrayList<String> questionFile = readFileByLines(args[1]);
        SearchEngine searchEngine = new SearchEngine(commentFile);
        int totalCount = 0;
        try {
            for (int i = 0; i < questionFile.size(); i++) {
                ArrayList<String> engineResult = searchEngine.run(questionFile.get(i));
                if (engineResult.size() != 0) totalCount++;
                System.err.println("Question: " + questionFile.get(i) + "\nRelated: ");
                for (String r: engineResult) {
                    System.err.println(r);
                }
                System.err.println("===================");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(totalCount);
        System.out.println(questionFile.size());
        System.out.println(1.0 * totalCount / questionFile.size());
    }
}
