

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class test {
    @Test
    void whitespaceTest() {
        System.out.println("\r");
    }

    @Test
    void countTest() throws FileNotFoundException {
        File pFile = new File("./input/positiveStrings");
        File nFile = new File("./input/negativeStrings");
        String[] countString = count(pFile, nFile);
        File dataFile = new File("./data/data.txt");
        File countFile = new File("./input/count.txt");
        PrintWriter writer = new PrintWriter(countFile);
        Scanner in = new Scanner(dataFile);
        int count = 0;
        while (in.hasNextLine()) {
            count++;
            String line = in.nextLine();
            int pos = line.indexOf(' ');
            String tag = line.substring(0, pos);
            String regex = line.substring(pos, line.length()).trim();
            System.out.println(countString[count] + "\t" + regex);
            writer.write(countString[count] + "\t" + regex + "\n");
        }
        writer.close();
    }

    private String[] count(File pFile, File nFile) throws FileNotFoundException {
        if (pFile.isDirectory() && nFile.isDirectory()) {
            String[] result = new String[257];
            File[] pFiles = pFile.listFiles();
            File[] nFiles = nFile.listFiles();
            for (int i = 0; i < pFiles.length; i++) {
                int pCount = countSingleFile(pFiles[i]);
                int nCount = countSingleFile(nFiles[i]);
                //System.out.println(pFiles[i].getName() + '\t' + nFiles[i].getName());
                String fileName = pFiles[i].getName();
                int index = Integer.parseInt(fileName.substring(0, fileName.indexOf('.')));
                //System.out.println(index);
                result[index] = pCount + "\t" +  nCount + '\t' + (pCount + nCount);
            }
            return result;
        } else {
            return null;
        }

    }

    private int countSingleFile(File file) throws FileNotFoundException {
        int result = 0;
        if (file.exists() && !file.isDirectory()) {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                in.nextLine();
                result++;
                //System.out.println(file.getName() + ": " + result);
            }
            in.close();
        }
        return result;
    }
}
