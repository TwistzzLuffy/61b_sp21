package deque;

public class LinkedListDeque <Luffy> {
    private StaffNode sentinel;
    private int size;
    private StaffNode viceSent;

    public class StaffNode {
        public Luffy item;
        public StaffNode next;
        public StaffNode prev;

        public StaffNode(StaffNode p, Luffy i, StaffNode n) {
            item = i;
            next = n;
            prev = p;
        }
    }

    public LinkedListDeque() {
        sentinel = new StaffNode(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(Luffy x) {
        sentinel.next = new StaffNode(sentinel, x, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    public void addLast(Luffy x) {
        sentinel.prev = new StaffNode(sentinel.prev, x, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }

    public boolean isEmpty() {
        if (sentinel.next == sentinel)
            return true;
        return false;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        StaffNode p = sentinel;
        for (int s = 0; s < size; s++) {
            System.out.print(p.next.item);
            System.out.print(" ");
            p = p.next;
        }
        System.out.println();
    }

    public Luffy removeFirst() {
        if (this.isEmpty())
            return null;
        else {
            StaffNode p = sentinel;
            Luffy a = p.next.item;
            p.next = p.next.next;
            p.next.prev = sentinel;
            size -= 1;
            return a;
        }
    }

    public Luffy removeLast() {
        if (this.isEmpty())
            return null;
        else {
            StaffNode p = sentinel;
            Luffy a = p.prev.item;
            p.prev = p.prev.prev;
            p.prev.next = sentinel;
            size -= 1;
            return a;
        }
    }

    public Luffy get(int index) {
        int s = 0;
        StaffNode p = sentinel;
        while (s != index) {
            p = p.next;
            s++;
        }
        return p.next.item;
    }

    public Luffy getRecursive(int index) {
        if (isEmpty())
            return null;
        viceSent = viceSent.next;
        if (index == 0) {
            Luffy res = viceSent.item;
            viceSent = sentinel;
            return res;
        }
        return getRecursive(index - 1);
    }

}