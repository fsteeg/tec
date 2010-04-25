package ir1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Implementierung von Information-Retrieval ueber lineare Suche in allen Texten, mit einer Laufzeit
 * von O(p*q) bei einer Textlaenge von p (m Werke * n Wörter pro Werk) und q Suchwoertern.
 */
/**
 * A first approach to search: naive linear search with O(p*q) for text of length p and q query
 * words.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class LinearSearch implements InformationRetrievalSimple {

  private List<String> works;

  /**
   * @param corpus The corpus to search on
   */
  public LinearSearch(final CorpusSimple corpus) {
    this.works = corpus.getWorks();
  }

  /**
   * {@inheritDoc}
   * @see ir1.InformationRetrievalSimple#search(java.lang.String)
   */
  public final Set<Integer> search(final String query) {
    long start = System.currentTimeMillis();
    Set<Integer> result = new HashSet<Integer>();
    List<String> queries = Arrays.asList(query.split(" "));
    /*
     * Wir betrachten jedes Wort in jedem Werk und vergleichen es mit jedem Suchbegriff, woraus sich
     * die Laufzeitkomplexitaet von O(p*q) ergibt, d.h. die Laege des Gesamttextes mal die Anzahl
     * der Suchbegriffe.
     */
    for (int i = 0; i < works.size(); i++) { // m
      List<String> words = Arrays.asList(works.get(i).split(" "));
      for (String word : words) { // m * n = p
        for (String q : queries) { // p * q
          if (word.equals(q.trim())) {
            result.add(i); // Implements OR
            break;
          }
        }
      }
    }
    System.out.println("Search took " + (System.currentTimeMillis() - start) + " ms.");
    return result;
  }

}
