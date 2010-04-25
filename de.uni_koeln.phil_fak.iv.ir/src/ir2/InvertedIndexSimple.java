package ir2;

import ir1.CorpusSimple;
import ir1.InformationRetrievalSimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Implementierung des InformationRetrieval-Interface mit einem invertierten Index, basierend auf
 * einem SortedSet.
 */
/**
 * An inverted index, the common data structure for information retrieval.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class InvertedIndexSimple implements InformationRetrievalSimple {
  /* Ein vorkonfigurierter Präprozessor: */
  private static final Preprocessor PREPROCESSOR = new Preprocessor();
  /* Der Intersection-Algorithmus von Manning et al.: */
  private static final Intersection INTERSECTION = Intersection.BOOK;

  private Map<String, SortedSet<Integer>> index = new HashMap<String, SortedSet<Integer>>();

  /**
   * @param corpus The corpus to build an index for.
   */
  public InvertedIndexSimple(final CorpusSimple corpus) {
    /* Wir erzeugen den Index aus dem Korpus: */
    long start = System.currentTimeMillis();
    index = index(corpus);
    System.out.println(String.format("Preprocessing index with %s types took %s ms.", index
        .keySet().size(), System.currentTimeMillis() - start));
  }

  private Map<String, SortedSet<Integer>> index(final CorpusSimple corpus) {
    List<String> works = corpus.getWorks();
    Map<String, SortedSet<Integer>> index = new HashMap<String, SortedSet<Integer>>();
    /* Wir indizieren jedes Werk: */
    for (int i = 0; i < works.size(); i++) {
      /* Wir benutzen den neuen Preprocessor bei der Indizierung: */
      List<String> types = PREPROCESSOR.tokenize(works.get(i));
      for (String type : types) {
        SortedSet<Integer> postings = index.get(type);
        /*
         * Falls wir noch keine Postings-Liste für den aktuellen Type haben legen wir eine an:
         */
        if (postings == null) {
          postings = new TreeSet<Integer>();
          index.put(type, postings);
        }
        /*
         * Wir indizieren das Wort, indem wir die ID des entsprechenden Dokuments in die passende
         * PostingsList einfügen:
         */
        postings.add(i);
      }
    }
    /*
     * Wir können zum Testen die Terme ausgeben und sehen dass mit unserem Präprozessor der Index
     * inzwischen ganz brauchbar ist:
     */
    // printSortedIndexTerms(index);
    return index;
  }

  @SuppressWarnings( "unused" )
  // optional
  private void printSortedIndexTerms(final Map<String, SortedSet<Integer>> index) {
    SortedSet<String> keys = new TreeSet<String>(index.keySet());
    for (String string : keys) {
      System.out.println(string);
    }
  }

  /**
   * {@inheritDoc}
   * @see ir1.InformationRetrievalSimple#search(java.lang.String)
   */
  public Set<Integer> search(final String query) {
    long start = System.currentTimeMillis();
    /*
     * Wir verarbeiten auch die Suchanfrage mit dem Präprozessor und können so Dinge einheitlich
     * behandeln (z.B. alles lower-case machen).
     */
    List<String> queries = PREPROCESSOR.tokenize(query);
    /*
     * Damit wir die Effizienz des Algorithmus aus Manning et al erreichen, müssen die einzelnen
     * Postings-Listen nach Länge sortiert sein. Dazu holen wir uns zunächst die Listen:
     */
    List<SortedSet<Integer>> allPostings = new ArrayList<SortedSet<Integer>>();
    for (String q : queries) {
      SortedSet<Integer> postings = index.get(q);
      allPostings.add(postings);
    }
    /* Sortieren diese dann nach ihrer Länge: */
    Collections.sort(allPostings, new Comparator<SortedSet<Integer>>() {
      public int compare(final SortedSet<Integer> o1, final SortedSet<Integer> o2) {
        return Integer.valueOf(o1.size()).compareTo(o2.size());
      }
    });
    /*
     * Ergebnis ist die Schnittmenge (Intersection) der ersten Liste... Hier behandeln wir die
     * Suchwörter als UND-Verknüpft!
     */
    SortedSet<Integer> result = allPostings.get(0);
    /* ...mit allen weiteren: */
    for (SortedSet<Integer> set : allPostings.subList(1, allPostings.size())) {
      result = INTERSECTION.of(result, set);
    }
    /*
     * Um wirklich zu sehen, ob Term-Dokument-Matrix und Postings-Listen unterschiedliche Laufzeit
     * haben, müsste man System.nanoTime verwenden; mit Millisekunden sehen wir aber, dass für
     * unseren Anwendungsfall es keinen Unterschied macht.
     */
    System.out.println(String.format("Search for '%s' took %s ms.", query, System
        .currentTimeMillis()
        - start));
    return result;
  }

}
