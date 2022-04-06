package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void testThreeAddThreeRemove() {
    AListNoResizing<Integer> ANR = new AListNoResizing<>();
    BuggyAList<Integer> BA = new BuggyAList<>();
    ANR.addLast(4);
    BA.addLast(4);
    ANR.addLast(5);
    BA.addLast(5);
    ANR.addLast(6);
    BA.addLast(6);
    int N = 3;
    for (int i = 0; i < N; i++) {
      assertEquals(ANR.removeLast(), BA.removeLast());
    }
  }
  @Test
  public void randomizedTest(){
    AListNoResizing<Integer> L = new AListNoResizing<>();

    int N = 500;
    for (int i = 0; i < N; i += 1) {
      int operationNumber = StdRandom.uniform(0, 3);
      if (operationNumber == 0) {
        // addLast
        if (L.size()<=0)
          break;
        int randVal = StdRandom.uniform(0, 100);
        L.addLast(randVal);
        System.out.println("addLast(" + randVal + ")");
        L.removeLast();
      } else if (operationNumber == 1) {
        // size
        int size = L.size();
        System.out.println("size: " + size);
      }
    }
  }
}