package ir2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Wir implementieren die Schnittmengenbildung als Interface mit zwei anonymen Implementierungen die
 * man direkt verwenden kann. Der Vorteil gegenüber statischen Methoden ist dass man auch direkt
 * eine neue Implementierung verwenden könnte, indem man eine anonyme Klasse übergibt (wie wir es
 * z.B. für das Sortieren der Listen mit einem anonymen Comparator gemacht haben). Sowas nennt man
 * auch ein Strategie-Interface, weil die Implementierungen beschreiben mit welcher Strategie das
 * Problem (hier die Schnittmengenbildung) gelöst wird, während das Problem selbst immer gleich ist
 * (im Interface definiert). Mehr dazu gibt es in Gamma et al, S. 315.
 */
/**
 * Interface for different algorithms that compute the intersection of two sorted sets of document
 * indexes, used for searching multiple query terms in an inverted index.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface Intersection {

  /**
   * @param pl1 The first postings list
   * @param pl2 The second postings list
   * @return The intersection of elements in the two given postings lists
   */
  SortedSet<Integer> of(SortedSet<Integer> pl1, SortedSet<Integer> pl2);

  /*
   * Hier eine Lösung unter Nutzung der Java-API: schön kompakt, aber Komplexität nicht in unserer
   * Hand (für's praktische Programmieren etwas gutes - die Java-Bibliotheken sind schnell und
   * werden laufend optimiert - bei besonderem Bedarf ev. (!) nicht genug)
   */
  /** Intersection implementation based on Java Collections API. */
  Intersection API = new Intersection() {

    public SortedSet<Integer> of(final SortedSet<Integer> pl1, final SortedSet<Integer> pl2) {
      List<Integer> result = new ArrayList<Integer>(pl1);
      result.retainAll(pl2);
      Collections.sort(result);
      return new TreeSet<Integer>(result);
    }

  };

  /*
   * Implementierung der Listen-Intersection, die die Sortierung der Listen ausnutzt, fast
   * Zeile-für-Zeile umgesetzt wie in Manning et al. 2008, S. 11, beschrieben. Hier haben wir einen
   * Algorithmus, der für unseren Anwendungsfall spezialisiert ist und positive Eigenschaften hat.
   */
  /** Intersection implementation based on the algorithm described in the IR book (Manning et al.). */
  Intersection BOOK = new Intersection() {

    public SortedSet<Integer> of(final SortedSet<Integer> pl1, final SortedSet<Integer> pl2) {
      List<Integer> answer = new ArrayList<Integer>();
      Iterator<Integer> i1 = pl1.iterator();
      Iterator<Integer> i2 = pl2.iterator();
      Integer p1 = nextOrNull(i1);
      Integer p2 = nextOrNull(i2);
      while (p1 != null && p2 != null) {
        if (p1.equals(p2)) {
          answer.add(p1);
          p1 = nextOrNull(i1);
          p2 = nextOrNull(i2);
        } else {
          if (p1 < p2) {
            p1 = nextOrNull(i1);
          } else {
            p2 = nextOrNull(i2);
          }
        }
      }
      Collections.sort(answer);
      return new TreeSet<Integer>(answer);
    }

    /* Ein wenig müssen wir uns verbiegen um nah am Pseudocode zu bleiben: */
    private Integer nextOrNull(final Iterator<Integer> i1) {
      return i1.hasNext() ? i1.next() : null;
    }

  };

}
