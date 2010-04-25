package ir1;

import java.util.Set;

/*
 * Information-Retrieval in der einfachsten Form: Ermitteln einer Menge von Dokumenten-IDs, die
 * einer Suchanfrage entsprechen.
 */
/**
 * Common interface for different information retrieval algorithms.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface InformationRetrievalSimple {

  /**
   * @param query The search query
   * @return A set of document indexes, the result for the given search query
   */
  Set<Integer> search(String query);

}
