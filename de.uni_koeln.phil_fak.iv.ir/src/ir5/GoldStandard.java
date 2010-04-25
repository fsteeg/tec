package ir5;

import ir2.Preprocessor;
import ir4.InformationRetrieval;
import ir4.Document;

import java.util.ArrayList;
import java.util.List;

/*
 * Erstellung eines Dummy-Goldstandards.
 */
/**
 * Creation of a mock gold standard for testing.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class GoldStandard {
  private GoldStandard() {} // enforce non-instantiability

  /**
   * @param index The index
   * @param query The query
   * @return A selection of documents from the given index that are relevant for the given query
   */
  public static List<Document> create(final InformationRetrieval index, final String query) {
    List<Document> result = new ArrayList<Document>();
    List<String> q = new Preprocessor().tokenize(query);
    for (Document d : index.getWorks()) {
      /*
       * Für unsere Experimente mit P, R und F betrachten wir ein Dokument nur dann als relevant,
       * wenn ein Elemente der Anfrage im Titel des Dokuments enthalten ist:
       */
      if (containsAny(d.getTitle(), q)) {
        result.add(d);
      }
    }
    return result;
  }

  private static boolean containsAny(final String title, final List<String> query) {
    for (String token : query) {
      /* Wir geben true zurück wenn ein Element der Anfrage im Titel enthalten ist: */
      if (title.toLowerCase().contains(token.toLowerCase())) {
        return true;
      }
    }
    return false;
  }
}
