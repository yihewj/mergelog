/**
 * Created by a20023 on 12/8/2015.
 */

public class Main {
    public static void main(String[] args) {
       // MergeLog ml = new MergeLog();
        MultiQueueMergeLog ml = new MultiQueueMergeLog();
        ml.setInputFileList(args);
      //  ml.setInputFileList("aplogcat-events.txt", "aplogcat-kernel.txt", "aplogcat-main.txt", "aplogcat-radio.txt", "aplogcat-system.txt");
      //  ml.setInputFileList("aplogcat-events.txt", "aplogcat-kernel.txt", "aplogcat-main.txt", "aplogcat-radio.txt", "aplogcat-system.txt");
        ml.Process();
    }
}
