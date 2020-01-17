import org.omg.PortableInterceptor.INACTIVE;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.PriorityQueue;


public class ExternalMerge {

    public static Comparator<String> defaultcomparator = new Comparator<String>() {
        @Override
        public int compare(String r1, String r2) {
            if(r1 == null || r2 == null){
                return 1;
            }
            String[] temp1 = r1.split("[:]");
            String[] temp2 = r2.split("[:]");

            return temp1[0].compareTo(temp2[0]);
        }
    };

    public static long estimateAvailableMemory() {
        System.gc();
        Runtime r = Runtime.getRuntime();
        // Maximum_Mem allowed - Memory_currently used
        return r.maxMemory() - r.totalMemory() + r.freeMemory();
    }

    public static long estimateBlockSize(long sizeoffile, int maxtmpfiles, long maxMemory) {

        int temp = 0;
        if (sizeoffile % maxtmpfiles != 0){
            temp = 1;
        }
        long blocksize = sizeoffile / maxtmpfiles + temp;

        if (blocksize < maxMemory / 2) {
            blocksize = maxMemory / 2;
        }
        return blocksize;
    }


    public static long mergeSortedFiles(BufferedWriter fbw,  Comparator<String> cmp, List<BinaryFileBuffer> buffers) throws IOException {
        PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<>(12, new Comparator<BinaryFileBuffer>() {
            @Override
            public int compare(BinaryFileBuffer i, BinaryFileBuffer j) {
                return cmp.compare(i.peek(), j.peek());
            }
        });
        for (BinaryFileBuffer element : buffers) {
            if (!element.empty()) {
                pq.add(element);
            }
        }
        long rowcounter = 0;
        try {
            String lastLine = null;
            String lastVal = null;
            Integer lastPos = 0;
            int count = 0;
            int initCount = 0;
            while (pq.size() > 0) {
                BinaryFileBuffer bfb = pq.poll();
                String r = bfb.pop();
                if (cmp.compare(r, lastVal) == 0) {
                    String[] Temp = r.split("[:]");
                    if (count == 0) {
                        count = Integer.parseInt(Temp[0]);
                    }
                    count++;
                    if (Temp.length >= 3) {
                        String[] numTextSplit = Temp[1].split("-");
                        int curVal = Integer.parseInt(numTextSplit[0]);
                        if (lastLine == null) {
                            lastLine = lastVal + "|" + numTextSplit[0] + "-" + numTextSplit[1];
                        } else {
                            lastLine = lastLine + "|" + (curVal - lastPos) + "-" + numTextSplit[1];
                        }
                        lastPos = curVal;
                    } else {
                        //System.out.println("DEBUG" + r);
                    }
                } else {
                    if (lastVal != null) {
                        count++;
                            if (count < initCount) {
                                fbw.write(Integer.toString(initCount) + ":" + lastLine);
                            } else {
                                fbw.write(count + ":" + lastLine);
                            }
                            fbw.newLine();

                        int curVal = 0;
                        String[] Temp = r.split("[:]");
                        if (Temp.length >= 3) {
                            String[] numTextSplit = Temp[1].split("-");
                            curVal = Integer.parseInt(numTextSplit[0]);
                        }
                        count = 0;
                        lastPos = curVal;
                        lastLine = r;
                        initCount = Integer.parseInt(r.split("=")[0]);
                        lastLine = lastLine.replaceAll("[0-9]+[=]", "");

                    }
                    lastVal = r;

                    ++rowcounter;
                    if (bfb.empty()) {
                        bfb.fbr.close();
                    } else {
                        pq.add(bfb);
                    }
                }
            }
        }finally {
            fbw.close();
            for (BinaryFileBuffer bfb : pq) {
                bfb.close();
            }
        }
        return rowcounter;

    }

    public static long mergeSortedFiles(List<File> files, File outputfile,  Comparator<String> cmp, boolean append) throws IOException {
        ArrayList<BinaryFileBuffer> bfbs = new ArrayList<>();
        for (File f : files) {
            InputStream in = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));

            BinaryFileBuffer bfb = new BinaryFileBuffer(br);
            bfbs.add(bfb);
        }
        BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputfile, append), Charset.defaultCharset()));
        long rowcounter = mergeSortedFiles(fbw, cmp, bfbs);
        for (File f : files) {
            f.delete();
        }
        return rowcounter;
    }

    public static File sortAndSave(List<String> tmplist) throws IOException {
        tmplist = tmplist.parallelStream().sorted(defaultcomparator).collect(Collectors.toCollection(ArrayList<String>::new));

        File newtmpfile = File.createTempFile("sortInBatch", "flatfile");
        newtmpfile.deleteOnExit();
        OutputStream out = new FileOutputStream(newtmpfile);

        try (BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(out,Charset.defaultCharset()))) {
            String lastLine = null;
            String lastVal = null;
            Integer lastPos = 0;
            int count = 0;
            for (String r : tmplist) {
                if (defaultcomparator.compare(r, lastVal) == 0) {
                    count++;
                    String[] Temp = r.split("[:]");
                    if(Temp.length >= 2){
                        String[] numTextSplit = Temp[1].split("-");
                        int curVal = Integer.parseInt(numTextSplit[0]);
                        if(lastLine == null){
                            lastLine = lastVal + "|" + numTextSplit[0] + "-" + numTextSplit[1];
                        }else {
                            lastLine = lastLine + "|" + (curVal - lastPos) + "-" + numTextSplit[1];
                        }
                        lastPos = curVal;
                    }else{
                        //System.out.println("DEBUGG " + r);
                    }
                }else{
                    if(lastVal != null){
                        count++;
                        fbw.write(count + "=" + lastLine);
                        fbw.newLine();
                    }
                    String[] Temp = r.split("[:]");
                    int curVal = 0;
                    if(Temp.length >= 2){
                        String[] numTextSplit = Temp[1].split("-");
                        curVal = Integer.parseInt(numTextSplit[0]);
                    }
                    count = 0;
                    lastPos = curVal;
                    lastLine = r;
                }
                lastVal = r;
            }
        }
        return newtmpfile;
    }

    public static List<File> sortInBatch(File file) throws IOException {
        BufferedReader fbr = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()));
        long datalength = file.length();
        List<File> files = new ArrayList<>();
        long blocksize = estimateBlockSize(datalength, 4096, estimateAvailableMemory());

        try {
            List<String> tmplist = new ArrayList<>();
            String line = "";
            try {
                while (line != null) {
                    long currentblocksize = 0;
                    while ((currentblocksize < blocksize)
                            && ((line = fbr.readLine()) != null)) {
                        tmplist.add(line);
                        currentblocksize += StrEst.estimatedSizeOf(line);
                    }
                    files.add(sortAndSave(tmplist));
                    tmplist.clear();
                }
            } catch (EOFException oef) {
                if (tmplist.size() > 0) {
                    files.add(sortAndSave(tmplist));
                    tmplist.clear();
                }
            }
        } finally {
            fbr.close();
        }
        return files;
    }

}
class BinaryFileBuffer {

    BufferedReader fbr;
    private String cache;

    BinaryFileBuffer(BufferedReader r) throws IOException {
        this.fbr = r;
        reload();
    }
    void close() throws IOException {
        this.fbr.close();
    }

    boolean empty() {
        return this.cache == null;
    }

    String peek() {
        return this.cache;
    }

    String pop() throws IOException {
        String answer = peek().toString();
        reload();
        return answer;
    }

    private void reload() throws IOException {
        this.cache = this.fbr.readLine();
    }
}

class StrEst {

    private static int OBJ_HEADER;
    private static int ARR_HEADER;
    private static int INT_FIELDS = 12;
    private static int OBJ_REF;
    private static int OBJ_OVERHEAD;
    private static boolean IS_64_BIT_JVM;

    private StrEst() {
    }

    static {

        IS_64_BIT_JVM = true;
        String arch = System.getProperty("sun.arch.Data.model");
        if (arch != null) {
            if (arch.contains("32")) {
                IS_64_BIT_JVM = false;
            }
        }
        OBJ_HEADER = IS_64_BIT_JVM ? 16 : 8;
        ARR_HEADER = IS_64_BIT_JVM ? 24 : 12;
        OBJ_REF = IS_64_BIT_JVM ? 8 : 4;
        OBJ_OVERHEAD = OBJ_HEADER + INT_FIELDS + OBJ_REF + ARR_HEADER;

    }

    public static long estimatedSizeOf(String s) {
        return (s.length() * 2) + OBJ_OVERHEAD;
    }

}
