import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by a20023 on 12/18/2015.
 */
public class MergeQueue {
    static int dbgRec = 0;
    private class QueueUnit {
        PriorityQueue<MergeItem> queue = new PriorityQueue<>();
        long endItemTime = 0;
        long startItemTime = 0;
        private final static int JUMPTIME = 10000000; // 1 hour

        public boolean Add(MergeItem item) {
            if (ValidateItemInQueue(item) == false) {
                return false;
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
                startItemTime = item.time;
                return true;
            }

            if (item.time > startItemTime && item.time < endItemTime) {
                return true;
            }


            if ( Math.abs(item.time - endItemTime) > JUMPTIME) {
                return false;
            }
            return true;
        }
    }

    private LinkedList<QueueUnit>  queueLists = new LinkedList<>();


    public void Add(MergeItem item) {
        dbgRec++;
        Iterator<QueueUnit> it = queueLists.iterator();
        int i = 0;
        while (it.hasNext()) {
            QueueUnit qunit = it.next();
            if (qunit.Add(item)) {
                item.debugInfo = "Q:" + i +" Rec NO: "+dbgRec;
                return;
            }
            i++;
        }

        QueueUnit qunit = new QueueUnit();
        qunit.Add(item);
        item.debugInfo = "Q:" + i +" Rec NO: "+dbgRec;
        queueLists.add(qunit);
        return;
    }

    public MergeItem Poll() {
        Iterator<QueueUnit> it = queueLists.iterator();
        while (it.hasNext()) {
            QueueUnit qunit = it.next();
            if (qunit.IsEmpty()) {
               // it.remove();
            } else {
                return qunit.Poll();
            }
        }
        return null;
    }

    public boolean IsEmpty() {
        if (queueLists.size() == 0){
            return false;
        }
        Iterator<QueueUnit> it = queueLists.iterator();
        while (it.hasNext()) {
            QueueUnit qunit = it.next();
            if (!qunit.IsEmpty()) {
                return false;
            }
        }
        return true;
    }

}
