/** Array based list.
 *  @author Josh Hug
 */
package deque;
public class ArrayDeque<T> {
    private T[] item;
    private int size;
    private int nextFirst=3;
    private int nextLast=4;

    /** Creates an empty list. */
    public ArrayDeque() {
        item =(T[]) new Object[8];
        size = 0;
    }

    public void resize(int capcacity){
        T[] a = (T[]) new Object[capcacity];
        System.arraycopy(item,0,a,0,item.length);
        item = a;
    }

    public void addFirst(T x){
        if (size == item.length){
            resize(size*2);
            nextFirst = item.length-1;
            nextLast = size;
        }
        item[nextFirst] = x;
        size +=1;
        nextFirst -=1;
        if (nextFirst == -1)
            nextFirst = item.length-1;
    }

    public void addLast(T x){
        if (size == item.length){
            resize(size*2);
            nextFirst = item.length-1;
            nextLast = size;
        }
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

    public boolean TorF(){
        float R = size/item.length;
        if(R<0.25)
            return true;
        return false;
    }

    public void minArray(int capcacity){
        T[] a = (T[]) new Object[capcacity];
        System.arraycopy(item,0,a,0,size);
        item = a;
    }

    public T removeFirst(){
        if (isEmpty())
            return null;
        T a =item[nextFirst+1];
        item[nextFirst+1] = null;
        size -= 1;
        nextFirst = nextFirst+1;
        if (nextFirst == item.length)
            nextFirst = 0;
        if (TorF()){
            int min = size/2;
            minArray(min);
        }
        return a;
    }

    public T removeLast(){
        if (isEmpty())
            return null;
        T a =item[nextLast-1];
        item[nextLast-1] = null;
        size -= 1;
        nextLast = nextLast-1;
        if(nextLast == -1)
            nextLast = item.length-1;
        if (TorF()){
            int min = size/2;
            minArray(min);
        }
        return a;
    }

    public T get(int index){
        return item[index];
    }


}
