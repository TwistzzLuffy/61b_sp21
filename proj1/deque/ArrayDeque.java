/** Array based list.
 *  @author Josh Hug
 */
package deque;
public class ArrayDeque<T> {
    private T[] item;
    private int size;
    private int nextFirst;
    private int nextLast;

    /** Creates an empty list. */
    public ArrayDeque() {
        item =(T[]) new Object[8];
        size = 0;
    }
    public void addFirst(T x){
        item[nextFirst] = x;
        size +=1;
        nextFirst -=1;
        if (nextFirst == -1)
            nextFirst = item.length-1;
    }

    public void addLast(T x){
        item[nextLast] = x;
        size +=1;
        nextLast +=1;
        if (nextLast == item.length)
            nextLast = 0;
    }

    public boolean isEmpty(){
        if(nextFirst == nextLast)
            return true;
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int i = nextFirst+1;
        for (;i<=nextLast;i++){
            System.out.print(item[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    public T removeFirst(){
        T a =item[nextFirst+1];
        item[nextFirst+1] = null;
        size -= 1;
        nextFirst = nextFirst+1;
        if (nextFirst == item.length)
            nextFirst = 0;
        return a;
    }

    public T removeLast(){
        T a =item[nextLast-1];
        item[nextLast-1] = null;
        size -= 1;
        nextLast = nextLast-1;
        if(nextLast == -1)
            nextLast = item.length-1;
        return a;
    }

    public T get(int index){
        return item[index];
    }

}
