package ir3;

/*
 * Der Nachteil von Memoisierung ist, dass sie wegen der rekursiven Aufrufe immer noch relativ
 * langsam ist. Die rekursiven Aufrufe können aber komplett gespart werden, wenn man alle
 * Kombinationen bottom-up (von den Teilstrings ausgehend) berechnet, statt wie bei der Memoisierung
 * top-down, von den Gesamtstrings ausgehend. Statt die Map wie bei der Memoisierung beim rekursiven
 * Durchlaufen nach und nach zu bestücken, füllen wir von vornherein eine Tabelle, die diese Werte
 * (d.h. Zwischenlösungen für 'i, j'-Paare) enthält. Diese Technik nennt sich 'Dynamic Programming'
 * und bringt hier nochmal einen beträchtlichen Zugewinn gegenüber der Lösung mit Memoisierung. Das
 * ist wunderbar schnell, dafür ist die Lösung nicht mehr so intuitiv, wobei man bei einer aus der
 * rekursiven Lösung entwickelten DP-Lösung (wie hier) die rekursive Relation noch sehr gut beim
 * Füllen der DP-Tabelle wiedererkennt.
 */
/**
 * Implementation of edit distance computation based on dynamic programming.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class DynamicProgrammingEditDistance implements EditDistance {

  @Override
  public int distance(final String s1, final String s2) {
    int[][] table = new int[s1.length() + 1][s2.length() + 1];
    /* Wir füllen einmal die Tabelle, d.h. lineare Laufzeit: O(i + 1 + j + 1) */
    for (int i = 0; i < table.length; i++) {
      for (int j = 0; j < table[i].length; j++) {
        /* "Base Condition" */
        if (i == 0) {
          table[i][j] = j;
        } else if (j == 0) {
          table[i][j] = i;
        } else {
          int del = table[i - 1][j] + 1;
          int ins = table[i][j - 1] + 1;
          int rep = table[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1);
          table[i][j] = Math.min(del, Math.min(ins, rep));
        }
      }
    }
    /*
     * Nachdem wir "bottom" bei 0,0 gestartet waren, sind wir am Ende "up" (an der Stelle, die die
     * Distanz der beiden Gesamtstrings anzeigt, also unten rechts in der Tabelle), d.h. bei der
     * gesuchten Gesamtdistanz D(i, j):
     */
    return table[s1.length()][s2.length()];
  }

}
