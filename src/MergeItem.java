
/**
 * Created by Yi He on 12/18/2015.
 */
public class MergeItem implements Comparable<MergeItem>{
    int fileInd;
    long time;
    String data;
    long index;
    public String debugInfo;
    int pid;
    int tid;

    MergeItem(int ind, long t, String line, long no) {
        fileInd = ind;
        time = t;
        data = line;
        index = no;
    }


    @Override
    public int compareTo(MergeItem o) {
        if (o.time < time) {
            return 1;
        }
        if (o.time > time) {
            return -1;
        }

        if (index < o.index) {
            return -1;
        }
        return  1;

    }
}
