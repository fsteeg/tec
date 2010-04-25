package ir3;

/*
 * Editierdistanz: die minimale Anzahl von Operationen um zwei Strings S1 und S2 aneinander
 * anzugleichen: ein Maß für die Nähe oder Ähnlichkeit von zwei Strings.
 */
/**
 * Common interface for different implementations of the edit distance comutation.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface EditDistance {

  /**
   * @param s1 The first string
   * @param s2 The second string
   * @return The minimum number of edits (insert, delete, replace) required to turn s1 into s2 or
   *         vice versa.
   */
  int distance(String s1, String s2);

}
