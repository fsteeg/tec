package ir3;

import java.util.HashMap;
import java.util.Map;

/*
 * Ein erster, großer Schritt: Die mehrfach auftretenden Kombinationen werden in einer Map abgelegt
 * und bei Bedarf abgefragt, statt sie immer wieder (und wieder rekursiv) neu zu berechnen. Diese
 * Technik nennt sich Memoisierung (en. memoization) und bringt hier schon einen beträchtlichen
 * Gewinn. Das Schöne bei der Memoisierung ist, dass die intuitiv entwickelte, rekursive Lösung
 * konzeptuell beibehalten aber optimiert wird (im Gegensatz zur Dynamic-Programming-Lösung, die die
 * rekursive Lösung nur simuliert, siehe DynamicProgrammingEditDistance.java).
 */
/**
 * Implementation of edit distance computation based on memoized recursion.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class MemoizedEditDistance extends RecursiveEditDistance {

  private Map<String, Integer> map = new HashMap<String, Integer>();

  @Override
  public int distance(final String s1, final String s2) {
    map.clear();
    return super.distance(s1, s2);
  }

  @Override
  protected int distance(final int i, final int j) {
    String pair = i + ", " + j;
    /*
     * Nur wenn wir das Paar noch nicht gelöst haben, verweisen wir an die Superklasse, die rekursiv
     * die Lösung für das Paar sucht, aber so für jedes Paar eben nur einmal:
     */
    if (!map.containsKey(pair)) {
      map.put(pair, super.distance(i, j));
    }
    return map.get(pair);
  }
}
