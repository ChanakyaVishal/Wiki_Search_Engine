package SecondaryIndex;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FileWriter {
    private static PrintWriter writer = null;

        public static PrintWriter getWriter() {
            if (writer == null) {
                initWriter();
            }
            return writer;
        }

        public static void initWriter() {
            try {
                writer = new PrintWriter("secondaryIndex", "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public static void closeWriter(){
            writer.close();
        }
}
