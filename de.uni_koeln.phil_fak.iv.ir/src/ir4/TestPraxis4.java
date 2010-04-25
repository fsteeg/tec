package ir4;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/* Praxis 4: Vektorraummodell, TF-IDF und Kosinusähnlichkeit für Ranking. */
/**
 * Tests for Information Retrieval, exercise 4: vector space model, TD-IDF and ranking.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class TestPraxis4 {

  private static List<Document> result;
  private static String query;
  private static InformationRetrieval index;

  /*
   * Die mit "BeforeClass" annotierte Methode (muss static sein) wird vor dem Ausführen aller Tests
   * einmal ausgeführt (analog dazu: "AfterClass"). So kann man die Umgebung der Tests erstellen.
   */
  /**
   * Setup the index and perform a basic query.
   */
  @BeforeClass
  public static void setup() {
    System.out.println("Building index...");
    /*
     * Unser Shakespeare-Korpus: Wir splitten an den Jahreszahlen und nehmen als Titel den Anfang
     * eines jeden Textes bis zu Stelle "\n", da beim splitten an den Jahreszahlen jeder Text mit
     * dem Titel beginnt, in einer eigenen Zeile.
     */
    index = new InvertedIndex(new Corpus("shaks12.txt", "1[56][0-9]{2}\n", "\n"));
    final int min = 20;
    Assert.assertTrue(index.getWorks().size() > min);
    query = "Caesar Brutus";
    result = new ArrayList<Document>(index.search(query));
  }

  /* Wir können vor jedem und nach jedem Test ein paar Info-Ausgaben vornehmen: */

  /**
   * Print a visual separator before each individual test.
   */
  @Before
  public void printOverview() {
    System.out.println("--------------------------------------------------");
  }

  /**
   * Print the result list after each test.
   */
  @After
  public void printResult() {
    for (Document document : result) {
      System.out.println(document);
    }
  }

  /* Dadurch können wir die Tests selbst recht kurz halten: */

  /**
   * Test the unranked result.
   */
  @Test
  public void resultUnranked() {
    System.out.println(String.format("%s unranked results for '%s':", result.size(), query));
    Assert.assertTrue("Search should return results", result.size() > 0);
  }

  /**
   * Rank the result and test the new ordering.
   */
  @Test
  public void resultRanked() {
    /*
     * Wir sortieren die Ergebnisse mit unserem VectorRanker, der nach Ähnlichkeit zum übergebenen
     * Dokument (hie für unseren Query) sortiert:
     */
    Collections.sort(result, new VectorRanker(new Document("Query", query), index));
    Assert.assertEquals("THE TRAGEDY OF JULIUS CAESAR", result.get(0).getTitle());
    System.out.println(String.format("%s ranked results for '%s':", result.size(), query));
  }

}