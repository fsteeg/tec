package ir3;

/*
 * Berechnung der Editierdistanz, die ineffizienteste aber intuitive Variante: Rekursive Berechnung
 * für alle Substring-Distanzen, dabei werden die bei der Rekursion mehrfach auftretenden
 * Kombinationen immer wieder neu (und wieder rekursiv) berechnet (zum sehen println
 * einkommentieren).
 */
/**
 * Implementation of edit distance computation based on simple recursion (very inefficient).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class RecursiveEditDistance implements EditDistance {

  private String s1;
  private String s2;

  /**
   * This implementation calls {@link #distance(int, int)} internally.
   * <p/>
   * {@inheritDoc}
   * @see ir3.EditDistance#distance(java.lang.String, java.lang.String)
   */
  @Override
  public int distance(final String s1, final String s2) {
    this.s1 = s1;
    this.s2 = s2;
    /* Gesamtproblem D(i,j) für i = |S1| und j = |S2|, d.h. Einstieg: */
    return distance(s1.length(), s2.length());
  }

  /**
   * This implementation calls itself recursively.
   * @param i The number of characters in s1
   * @param j The number of characters in s2
   * @return The distance of the first i characters in s1 to the first j characters in s2
   */
  protected int distance(final int i, final int j) {
    // System.out.println(String.format("Checking pair: %s, %s", i,j));
    /* "Base Condition" */
    if (i == 0) {
      return j;
    }
    if (j == 0) {
      return i;
    }
    /* "Recurrence Relation" */
    if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
      return distance(i - 1, j - 1);
    }
    /* Für jede Änderung x steigen wir dreimal rekursiv ab, d.h. exponentielle Laufzeit: O(3^x) */
    int del = distance(i - 1, j) + 1;
    int ins = distance(i, j - 1) + 1;
    int rep = distance(i - 1, j - 1) + 1;
    return Math.min(del, Math.min(ins, rep));
  }

}
