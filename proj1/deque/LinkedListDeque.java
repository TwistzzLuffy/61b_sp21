package deque;

public class LinkedListDeque <Luffy>{
    private StaffNode sentinel;
    private int size;

    public class StaffNode{
        public Luffy item;
        public StaffNode next;
        public StaffNode prev;

        public StaffNode(StaffNode p,Luffy i,StaffNode n){
            item = i;
            next = n;
            prev = p;
        }
    }
    public LinkedListDeque(){
        sentinel =new StaffNode(null,null,null);
        sentinel.prev=sentinel;
        sentinel.next=sentinel;
        size=0;
    }
    public void addFirst(Luffy x){
        sentinel.next=new StaffNode(sentinel,x,sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size +=1;
    }
    public void addLast(Luffy x){
        sentinel.prev=new StaffNode(sentinel.prev,x,sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size +=1;
    }

    public boolean isEmpty(){
        if (sentinel.next == sentinel.prev)
            return true;
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        StaffNode p = sentinel;
        for (int s =0;s<size;s++){
            System.out.print(p.next.item);
            System.out.print(" ");
            p = p.next;
        }
        System.out.println();
    }

    public Luffy removeFirst(){
        StaffNode p = sentinel;
        Luffy a = p.next.item;
        p.next = p.next.next;
        p.next.prev=sentinel;
        if(this.isEmpty())
            return null;
        else{
            size -= 1;
            return  a;
        }
    }

    public Luffy removeLast(){
        StaffNode p = sentinel;
        Luffy a = p.prev.item;
        p.prev = p.prev.prev;
        p.prev.next = sentinel;
        if(this.isEmpty())
            return null;
        else{
            size -= 1;
            return  a;
        }
    }

    public Luffy get(int index){
        int s = 0;
        StaffNode p = sentinel;
        while(s != index){
            p=p.next;
            s++;
        }
        return p.next.item;
    }
    public static void main(String[] args) {
        LinkedListDeque<Integer> L = new LinkedListDeque<Integer>();

        L.addFirst(5);
        L.addLast(6);
        L.addLast(7);
        L.addFirst(4);
        System.out.println(L.size());
        L.printDeque();
        System.out.println(L.removeFirst());
        System.out.println(L.removeLast());
        L.printDeque();
        System.out.println(L.get(1));
        System.out.println(L.isEmpty());
    }
}
