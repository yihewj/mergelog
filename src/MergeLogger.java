import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yi He on 12/18/2015.
 */
public class MergeLogger {
    private static final long INVALIDTIME = 0;
    static int progressind = 0;
    private final int MAXFILESNUM = 10;
    private final int TORLENTNUM = 20;
    String mergedFileName = "merge.txt";
    ArrayList<String> inputList = new ArrayList<>();
    MergeQueue mqueue = new MergeQueue();
    ArrayList<BufferedReader> fileHandleList = new ArrayList<>();
    ArrayList<MergeItem> logLineArray = new ArrayList<>();
    long lineIndex = 0;
    long lastLogTime[] = new long[MAXFILESNUM];
    private int mergeFileNum = 0;
    private boolean DEBUG = false;

    public LogLine convertLog(String line) {
        LogLine logLine = new LogLine();
        logLine.time = INVALIDTIME;

        String logPattern = "^([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2}).([0-9]{3})\\s*([0-9]{1,5})\\s*([0-9]{1,5})\\s{1,}(\\p{Upper}{1})\\s{1,}(\\w*)\\s*:(.*?)$";
        Matcher match = Pattern.compile(logPattern).matcher(line);
        if (match.find()) {
            String timestamp = match.group(1) + match.group(2) + match.group(3) + match.group(4) + match.group(5) + match.group(6);
            logLine.time = Long.parseLong(timestamp);
            logLine.pid = Integer.parseInt(match.group(7));
            logLine.tid = Integer.parseInt(match.group(8));
            logLine.logTag = match.group(10);
            logLine.logContent = match.group(11);
        }

        return logLine;
    }

 /*   public long getLogTime(String line) {
        long time = INVALIDTIME;
        String timePattern = "^([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2}).([0-9]{3}) (([0-9]*))";
        Matcher match = Pattern.compile(timePattern).matcher(line);
        if (match.find()) {
            String timestamp = match.group(1) + match.group(2) + match.group(3) + match.group(4) + match.group(5) + match.group(6);
            time = Long.parseLong(timestamp);
        }
        return time;
    }*/

  /*  public long getLogTime(String line) {
        LogLine data = convertLog(line);
        return  data.time;
    }*/

    private int InitQueWithLog(int lineOfFile) throws IOException {
        int readLineNum = 0;

        //Need file loop inside
        for (int lineNo = 0; lineNo < lineOfFile; lineNo++) {
            for (int fileInd = 0; fileInd < fileHandleList.size(); fileInd++) {
                String line = fileHandleList.get(fileInd).readLine();

                if (line == null) {
                    continue;
                }
                lineIndex++;
                LogLine data = convertLog(line);
                long logTime =  data.time;
                if (logTime == INVALIDTIME) {
                    continue;
                }

                MergeItem item = new MergeItem(fileInd, logTime, line, lineIndex);
                mqueue.Add(item);
                readLineNum++;
            }
        }

        return readLineNum;
    }

    //am_proc_start
    //am_proc_died

    private int ReadLogFromFile(int fileID, int lineNum) throws IOException {
        int readLineNum = 0;

        for (int i = 0; i < lineNum; i++) {
            String line = fileHandleList.get(fileID).readLine();
            if (line == null) {
                continue;
            }
            LogLine data = convertLog(line);
            long logTime =  data.time;
            if (logTime == INVALIDTIME) {
                i--;
                continue;
            }

            MergeItem item = new MergeItem(fileID, logTime, line, lineIndex);
            int res = mqueue.Add(item);
            if (MergeQueue.NEWQUEUE == res) {
                //IF create new queue. Means we met jump data and need read more in the file
                ReadLogFromFile(fileID, TORLENTNUM);
            }
            readLineNum++;
        }

        return readLineNum;
    }

    void setInputFileList(String... args) {
        for (String arg : args) {
            File inputFile = new File(arg);
            if (!inputFile.exists() || inputFile.isDirectory()) {
                System.out.println("File " + arg + " doesn't exist!");
                continue;
            }
            inputList.add(arg);
            mergeFileNum++;
            try {
                BufferedReader br = new BufferedReader(new FileReader(arg));
                fileHandleList.add(br);
            } catch (FileNotFoundException e) {
                System.out.println("Couldn't open file " + arg);
                e.printStackTrace();
                return;
            }
        }
    }

    private void showProgess() {
        progressind++;
        if (progressind == 100) {
            System.out.print(".");
            progressind = 0;
        }
    }

    public long Process() {
        long totalLine = 0;
        try {
            int readNum = InitQueWithLog(TORLENTNUM);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(mergedFileName, false)));
            while (true) {
                showProgess();
                MergeItem item = mqueue.Poll();
                if (item != null) {
                    if (DEBUG) {
                        writer.println(item.fileInd + " " + item.debugInfo + " " + item.data);
                    } else {
                        writer.println(item.data);
                    }
                    ReadLogFromFile(item.fileInd, 1);
                } else {
                    break;
                }

            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalLine;
    }


}
