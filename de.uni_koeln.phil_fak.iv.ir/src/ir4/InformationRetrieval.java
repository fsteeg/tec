package ir4;


import java.util.List;
import java.util.Set;

/* Repräsentation eines Index für Information-Retrieval. */
/**
 * Common interface for different information retrieval algorithms.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface InformationRetrieval {

  /**
   * @param query The search query
   * @return A set of document indexes, the result for the given search query
   */
  Set<Document> search(String query);

  /**
   * @return The terms this information retrieval instance consists of
   */
  Set<String> getTerms();

  /**
   * @return The works this information retrieval instance consists of
   */
  List<Document> getWorks();

  /**
   * @param t The term
   * @return The document frequency of t, that is: the number of documents that contain t
   */
  Integer getDocumentFrequency(String t);

}
