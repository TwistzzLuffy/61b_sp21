/** Array based list.
 *  @author Josh Hug
 */
package deque;
public class ArrayDeque<T> {
    private T[] item;
    private int size;
    private int nextFirst=3;
    private int nextLast=4;
    private int length;

    /** Creates an empty list. */
    public ArrayDeque() {
        length =8;
        item =(T[]) new Object[length];
        size = 0;
        nextFirst = length/2;
        nextLast = length/2+1;
    }

    public void resize(int capcacity){
        T[] a = (T[]) new Object[capcacity];
        if (capcacity > length || nextFirst > nextLast){
            System.arraycopy(item,getIndex(nextFirst+1),
                    a,0,length - getIndex(nextFirst+1));
            System.arraycopy(item,length - getIndex(nextFirst+1),
                    a,length - getIndex(nextFirst+1),getIndex(nextLast - 1) + 1);
        }
        else{
            System.arraycopy(item,getIndex(nextFirst+1),a,0,size);
        }
    }

    private int getIndex(int i){
        return (i+length) % length;
    }

    public void addFirst(T x){
        if (nextFirst == nextLast){
            resize(size*2);
        }
        item[nextFirst] = x;
        size +=1;
        nextFirst = getIndex(nextFirst+1);
    }

    public void addLast(T x){
        if (nextFirst == nextLast){
            resize(size*2);
        }
        item[nextLast] = x;
        size +=1;
        nextLast = getIndex(nextLast-1);
    }


    public int size(){
        return size;
    }

    public void printDeque(){
        int i = nextFirst+1;
        for (;i<=nextLast;i++){
            System.out.print(item[i] + " ");
        }
        System.out.println();
    }


    public T removeFirst(){
        int nextIndex = getIndex(nextFirst+1);
        T result = item[nextIndex];
        if(result != null){
            item[nextIndex] = null;
            nextFirst = nextIndex;
            size--;
        }
        if(size <0.25*length && size >16){
            resize(length / 2);
        }
        return result;
    }

    public T removeLast(){
        int nextIndex = getIndex(nextLast - 1);
        T result = item[nextIndex];
        if(result != null){
            item[nextIndex] = null;
            nextLast = nextIndex;
            size--;
        }
        if(size <0.25*length && size >16){
            resize(length / 2);
        }
        return result;
    }

    public T get(int index){
        int i = getIndex(nextFirst + 1 +index );
        return item[i];
    }

    public boolean isEmpty() {
        return size == 0;
    }


}
