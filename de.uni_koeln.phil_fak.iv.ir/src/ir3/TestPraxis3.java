package ir3;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/*
 * IR, Praxissitzung 3: Editierdistanz und algorithmische Techniken: Divide and Conquer, Rekursion,
 * Memoisierung und Dynamic Programming, vgl. Gusfield 1997, Kap. 11 (in der Institutsbibliothek
 * verfügbar) und Manning et al. 2008, Kap. 3 (informationretrieval.org).
 */
/**
 * Test for the different edit distance implementations.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class TestPraxis3 {

  /**
   * Tests the correctness of the simple recursive solution.
   */
  @Test
  public void distanceRecursive() {
    runResultTest(new RecursiveEditDistance());
  }

  /**
   * Tests the correctness of the memoization solution.
   */
  @Test
  public void distanceMemoized() {
    runResultTest(new MemoizedEditDistance());
  }

  /**
   * Tests the correctness of the DP solution.
   */
  @Test
  public void distanceDynamicProgramming() {
    runResultTest(new DynamicProgrammingEditDistance());
  }

  /**
   * Tests the runtime performance of the simple recursive solution (takes some time).
   */
  @Test
  public void performanceRecursive() {
    runPerformanceTest(new RecursiveEditDistance());
  }

  /**
   * Tests the runtime performance of the memoization solution.
   */
  @Test
  public void performanceMemoized() {
    runPerformanceTest(new MemoizedEditDistance());
  }

  /**
   * Tests the runtime performance of the DP solution.
   */
  @Test
  public void performanceDynamicProgramming() {
    runPerformanceTest(new DynamicProgrammingEditDistance());
  }

  /**
   * Print a visual separator for the individual tests.
   */
  @After
  public void after() {
    System.out.println("-------------------------------------------------------------------------");
  }

  private void runResultTest(final EditDistance edit) {
    System.out.println("Running correctness test for: " + edit.getClass().getSimpleName());
    Assert.assertEquals(2, edit.distance("ehe", "reh"));
    Assert.assertEquals(2, edit.distance("eber", "leder"));
    Assert.assertEquals(0, edit.distance("ehe", "ehe"));
    Assert.assertEquals(0, edit.distance("", ""));
    Assert.assertEquals(1, edit.distance("ehe", "eher"));
    Assert.assertEquals(2, edit.distance("he", ""));
    Assert.assertEquals(2, edit.distance("", "he"));
    Assert.assertEquals(0, edit.distance("nette rehe retten", "nette rehe retten"));
  }

  private void runPerformanceTest(final EditDistance editDistance) {
    System.out.print("Running performance test for: " + editDistance.getClass().getSimpleName()
        + "...");
    long start = System.currentTimeMillis();
    String s1 = "nattern necken";
    String s2 = "nette rehe retten";
    final int runs = 100;
    for (int i = 0; i < runs; i++) {
      editDistance.distance(s1, s2);
    }
    System.out.println(String.format(" %s ms.", System.currentTimeMillis() - start));
  }

}
