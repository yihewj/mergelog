import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by a20023 on 12/18/2015.
 */
public class MultiQueueMergeLog {
    private final int MAXFILESNUM  = 10;
    private static final long INVALIDTIME = 0;

    String mergedFileName = "merge.txt";
    ArrayList<String> inputList = new ArrayList<>();
    MergeQueue mqueue = new MergeQueue();
    ArrayList<BufferedReader> fileHandleList = new ArrayList<>();
    private int mergeFileNum = 0;
    ArrayList<MergeItem> logLineArray = new ArrayList<>();
    long lineIndex = 0;
    long lastLogTime[] = new long [MAXFILESNUM];

    private int ReadLogFromFile() throws IOException {

        int readLineNum = 0;
        for (int lineNo = 0; lineNo < 5; lineNo++) {
            for (int fileInd = 0; fileInd < fileHandleList.size(); fileInd++) {
                String line = fileHandleList.get(fileInd).readLine();

                if (line == null) {
                    continue;
                }
                lineIndex++;
                long logTime = getLogTime(line);
                MergeItem item = new MergeItem(fileInd, logTime, line, lineIndex);
                logLineArray.add(item);
                readLineNum++;
            }
        }
        return readLineNum;
    }

    private void AddLogToQueue() {
        for (int i = 0; i < logLineArray.size(); i++) {
            mqueue.Add(logLineArray.get(i));
        }
        logLineArray.clear();
    }


    void setInputFileList(String... args) {
        for (int i = 0; i < args.length; i++) {
            File inputFile = new File(args[i]);
            if (!inputFile.exists() || inputFile.isDirectory()) {
                System.out.println("File "+args[i]+ " doesn't exist!");
                continue;
            }
            inputList.add(args[i]);
            mergeFileNum++;
            try {
                BufferedReader br = new BufferedReader(new FileReader(args[i]));
                fileHandleList.add(br);
            } catch (FileNotFoundException e) {
                System.out.println("Couldn't open file "+args[i]);
                e.printStackTrace();
                return;
            }
        }
    }

    public static long getLogTime(String line) {
        long time = INVALIDTIME;
        String timePattern = "^([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2}).([0-9]{3}) (([0-9]*))";
        Matcher match = Pattern.compile(timePattern).matcher(line);
        if (match.find()) {
            String timestamp = match.group(1)+match.group(2)+match.group(3)+match.group(4)+match.group(5)+match.group(6);
            time = Long.parseLong(timestamp);        }
        return time;
    }
    static int progressind = 0;
    private void showProgess() {
        progressind++;
        if (progressind  == 100) {
            System.out.print(".");
            progressind = 0;
        }
    }

    private boolean DEBUG = false;

    public long Process() {
        long totalLine  = 0;
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(mergedFileName, false)));
            while (true) {
               int readNum = ReadLogFromFile();
                showProgess();
                if (readNum == 0) {
                    while (!mqueue.IsEmpty()) {
                        MergeItem item = mqueue.Poll();
                        if (DEBUG) {
                            writer.println(item.fileInd + " " + item.debugInfo + " " + item.data);
                        } else {
                            writer.println(item.data);
                        }
                    }
                    break;
                }
                AddLogToQueue();
                for (int i = 0; i < 5; i++) {
                    MergeItem item = mqueue.Poll();
                    if (item == null) {
                        break;
                    }
                    if (DEBUG) {
                        writer.println(item.fileInd + " " + item.debugInfo + " " + item.data);
                    } else {
                        writer.println(item.data);
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalLine;
    }


}
