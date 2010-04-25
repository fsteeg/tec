package ir1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Implementierung von Information-Retrieval ueber eine Term-Dokument-Matrix, die nach ihrem Aufbau
 * ueber Bit-Operationen eine Beantwortung von Anfragen in konstanter Zeit ermoeglicht.
 */
/**
 * Search implementation using a term-document-matrix with O(1) after construction.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class TermDocumentMatrix implements InformationRetrievalSimple {

  private boolean[][] matrix;
  private Map<String, Integer> pos;

  /**
   * @param corpus The corpus to search on
   */
  public TermDocumentMatrix(final CorpusSimple corpus) {
    long start = System.currentTimeMillis();
    List<String> works = corpus.getWorks();
    List<String> terms = initTerms(works);
    pos = initPositions(terms);
    /*
     * Falls beim Erstellen der Matrix der Speicher volllaeuft, kann man den Speicher, den sich der
     * Prozess nimmt, mit der Angabe des Parameters "-Xmx256m" (unter Open Run Dialog, Arguments, VM
     * Arguments) erhoehen, dies fuer 256 MB; per default nimmt sich jeder Java-Prozess 64 MB.
     */
    matrix = new boolean[terms.size()][works.size()];
    for (int i = 0; i < works.size(); i++) {
      for (String word : works.get(i).split(" ")) {
        /*
         * Wir tragen für jeden Token ein 'true' in die Matrix an der Position des
         * korrepsondierenden Types:
         */
        matrix[pos.get(word)][i] = true;
      }
    }
    System.out.println("Preprocessing took " + (System.currentTimeMillis() - start));
    printMatrix(terms, matrix);
  }

  private static void printMatrix(final List<String> terms, final boolean[][] matrix) {
    final int max = 20; // Integer.MAX_VALUE;
    for (int i = 0; i < Math.min(matrix.length, max); i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        System.out.print(matrix[i][j] ? "1 " : "0 ");
      }
      System.out.println("(" + terms.get(i).trim() + ") ");
    }
  }

  private List<String> initTerms(final List<String> works) {
    /*
     * Wir holen uns alle Tokens, erstellen daraus ein Set um alle Types zu bekommen und packen das
     * dann in eine Liste um wieder eine geordnete Sammlung zu erhalten, bei der wir auf eine
     * Position zugreifen koennen:
     */
    Set<String> termsSet = new HashSet<String>();
    for (String work : works) {
      termsSet.addAll(Arrays.asList(work.split(" ")));
    }
    return new ArrayList<String>(termsSet);
  }

  private Map<String, Integer> initPositions(final List<String> terms) {
    /*
     * Um nicht bei jedem Wort jedes Textes in der Liste aller Types immer neu nach dem aktuellen
     * Wort zu suchen, merken wir uns vorher die Positionen aller Types in einer Map, mit der wir
     * die Positionen beim Durchlaufen der Matrix in konstanter Zeit bekommen:
     */
    Map<String, Integer> positions = new HashMap<String, Integer>();
    for (int i = 0; i < terms.size(); i++) {
      positions.put(terms.get(i), i);
    }
    return positions;
  }

  /**
   * {@inheritDoc}
   * @see ir1.InformationRetrievalSimple#search(java.lang.String)
   */
  public final Set<Integer> search(final String query) {
    long start = System.currentTimeMillis();
    List<String> queries = Arrays.asList(query.split(" "));
    /*
     * Wir erstellen ein BitSet aus jedem Vektor fuer die Suchwoerter, mit dem wir Bit-Operationen
     * durchfuehren koennen. Fuer ints und longs koennten wir dies ueber die &, | und ^ Operatoren
     * tun, Zahlen mit ungefaehr 40 Stellen passen nicht mehr in ints (32-bit) aber in longs
     * (64-bit). Ein Alternative waere die Klasse BigInteger, die in Java ganze Zahlen beliebiger
     * Groesse fasst. Wenn weder sowas wie BitSet, noch sowas wie BigInteger verfuegbar sind, wuerde
     * man ueber die Arrays laufen und von Hand schauen, ob das Wort in jedem (&), in einem (|) oder
     * aber nicht (^) im Werk vorkommt.
     */
    BitSet bitSet = bitSetFor(matrix[pos.get(queries.get(0))]);
    Set<Integer> result = new HashSet<Integer>();
    for (String q : queries) {
      /* Das ist das Schöne an dieser Lösung: die boolschen Operationen sind einfach geschenkt: */
      bitSet.or(bitSetFor(matrix[pos.get(q)]));
    }
    /* Wir lesen das Ergebnis aus dem resultierenden BitSet aus: */
    for (int i = 0; i < matrix[0].length; i++) {
      if (bitSet.get(i)) {
        result.add(i);
      }
    }
    System.out.println("Search took " + (System.currentTimeMillis() - start));
    return result;
  }

  private BitSet bitSetFor(final boolean[] bs) {
    /* Wir erzeugen ein BitSet korrespondierend mit dem übergebenen boolean-Array: */
    BitSet set = new BitSet(bs.length);
    for (int i = 0; i < bs.length; i++) {
      if (bs[i]) {
        set.set(i);
      }
    }
    return set;
  }
}
