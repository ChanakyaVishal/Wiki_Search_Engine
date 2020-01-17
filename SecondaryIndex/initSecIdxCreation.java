package SecondaryIndex;

import java.io.*;

public class initSecIdxCreation {
    public static void main (String args[]) {
        FileWriter fileWriter = new FileWriter();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("nOutput"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try{
        String readLine = "";
        int strIdx = 0;
        int prevIdx = 0;
        boolean first = true;

        boolean change = true;
        String preVal = "";
            while ((readLine = reader.readLine()) != null) {
            String[] valArr = readLine.split("[:]");
            if(valArr.length >= 3) {
                String val = valArr[1];
                int idx = Integer.parseInt(valArr[2].split("[-]")[0]);
                if (!first && !val.substring(0,3).equals(preVal.substring(0,3))) {
                    change = true;
                }

                if (change) {
                    if(first){
                         first = false;
                    }
                    else {
                        FileWriter.getWriter().println(preVal.substring(0,3) + " " + Integer.toString(strIdx) + " " + Integer.toString(prevIdx));
                    }
                    strIdx = idx;
                    preVal = val;
                    change = false;
                }
                prevIdx = idx;
            }
        }
        }catch (IOException e) {
                e.printStackTrace();
        }finally {
            if (FileWriter.getWriter() != null) {
                FileWriter.closeWriter();
            }
        }
    }
}
