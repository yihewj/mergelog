/**
 * Created by Yi He on 12/8/2015.
 */

public class Main {
    public static void main(String[] args) {
       // MergeLog ml = new MergeLog();
        MergeLogger ml = new MergeLogger();
        ml.setInputFileList(args);
      //  ml.setInputFileList("aplogcat-events.txt", "aplogcat-kernel.txt", "aplogcat-main.txt", "aplogcat-radio.txt", "aplogcat-system.txt");
      //  ml.setInputFileList("aplogcat-events.txt", "aplogcat-kernel.txt", "aplogcat-main.txt", "aplogcat-radio.txt", "aplogcat-system.txt");
        ml.Process();
    }
}
