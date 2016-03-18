import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by Yi He on 12/18/2015.
 */
public class MergeQueue {
    static int dbgRec = 0;
    private class QueueUnit {
        PriorityQueue<MergeItem> queue = new PriorityQueue<>();
        long endItemTime = 0;
        long startItemTime = 0;
        private final static int JUMPTIME = 10000000; // 1 hour

        public boolean Add(MergeItem item) {
            if (!ValidateItemInQueue(item)) {
                return false;
            }
            if (startItemTime == 0) {
                startItemTime = item.time;
            }
            queue.add(item);
            if (item.time > endItemTime) {
                endItemTime = item.time;
            }
            return true;
        }

        public MergeItem Poll() {
            MergeItem item = queue.poll();
            if (item != null) {
                startItemTime = item.time;
            }
            return item;
        }

        public boolean IsEmpty () {
            return (queue.size() == 0);
        }

        private boolean ValidateItemInQueue(MergeItem item) {
            if (endItemTime == 0) {  //It is a new Queue

                return true;
            }


            return Math.abs(item.time - endItemTime) <= JUMPTIME;
        }
    }

    private LinkedList<QueueUnit>  queueLists = new LinkedList<>();

    public static int SAMEQUE = 0;
    public static int NEWQUEUE = 1;


    public int Add(MergeItem item) {
        dbgRec++;
        Iterator<QueueUnit> it = queueLists.iterator();
        int i = 0;
        while (it.hasNext()) {
            QueueUnit qunit = it.next();
            if (qunit.Add(item)) {
                item.debugInfo = "Q:" + i +" Rec NO: "+dbgRec;
                return SAMEQUE;
            }
            i++;
        }

        QueueUnit qunit = new QueueUnit();
        qunit.Add(item);
        item.debugInfo = "Q:" + i +" Rec NO: "+dbgRec;
        queueLists.add(qunit);
        return NEWQUEUE;
    }

    public MergeItem Poll() {
        for (QueueUnit qunit : queueLists) {
            if (!qunit.IsEmpty()) {
                return qunit.Poll();
            }
        }
        return null;
    }

    public boolean IsEmpty() {
        if (queueLists.size() == 0){
            return false;
        }
        for (QueueUnit qunit : queueLists) {
            if (!qunit.IsEmpty()) {
                return false;
            }
        }
        return true;
    }

}
