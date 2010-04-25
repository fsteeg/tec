package ir5;

import ir4.Corpus;
import ir4.Document;
import ir4.InformationRetrieval;
import ir4.InvertedIndex;
import ir4.VectorRanker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/* Praxis 5: Evaluierung und Ranking. */
/**
 * Tests for evaluation and ranking.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class TestPraxis5 {

  private static final String OUTPUT_TEXTILE = "src/ir5/output.textile";
  private static List<Document> result;
  private static String query;
  private static InformationRetrieval index;
  private static List<Document> gold;
  private static Evaluation evaluation;
  private static final int K_START = 5;
  private static final int K_END = 10;
  private static VectorRanker ranker;
  private static List<Document> ranked;

  /* Konzeptuell bestünde ein kompletter Goldstandard aus sowas: */
  private static class Topic {
    Topic(final String description, final List<String> keywords, final List<String> documents) {}
  }

  private static Topic royal = new Topic("Works of Shakespeare related to royal families", // Beschreibung
      Arrays.asList("King", "Prince", "Queen"), // Suchbegriffe
      Arrays.asList("Hamlet", "Othello", "King Henry")); // Relevante Werke
  private static Topic love = new Topic("Romantic works of Shakespeare", // Beschreibung
      Arrays.asList("Love", "Man", "Woman"), // Suchbegriffe
      Arrays.asList("Romeo and Juliet", "A midsummer night's dream")); // Relevante Werke

  /**
   * Set up corpus and index, perform a simple search, rank result, store raw and ranked result.
   */
  @BeforeClass
  public static void setup() {
    Assert.assertTrue("Sample topics should exist", royal != null && love != null);
    System.out.println("Building index...");
    index = new InvertedIndex(new Corpus("shaks12.txt", "1[56][0-9]{2}\n", "\n"));
    final int min = 20;
    Assert.assertTrue(index.getWorks().size() > min);
    query = "King Love";
    result = new ArrayList<Document>(index.search(query));
    /*
     * Für die Übung beschränken wir uns als Goldstandard auf eine Liste von Dokumenten für einen
     * Suchstring:
     */
    gold = GoldStandard.create(index, query);
    evaluation = new Evaluation(gold);
    /*
     * Das Ranking erfolgt durch Vergleich der Dokumentenvektoren mit dem Anfragevektor, dazu
     * erstellen wir aus der Anfrage einen Vektor (im Raum des Korpus, wie die echten Dokumente),
     * und übergeben das Dokument dem Ranker:
     */
    ranker = new VectorRanker(new Document("Query", query), index);
    ranked = new ArrayList<Document>(result);
    Collections.sort(ranked, ranker);
  }

  /**
   * Print a visual separation before each test.
   */
  @Before
  public void printSep() {
    System.out.println();
  }

  /**
   * Test the original, unranked result.
   */
  @Test
  public void resultUnranked() {
    /* Wir evaluieren zunächst alle Ergebnisse gegen den Goldstandard: */
    EvaluationResult allEval = evaluation.evaluate(result);
    Assert
        .assertTrue("Evaluation result for all results should be greater than 0", allEval.f() > 0);
    System.out.println("Unranked, all: \n" + allEval);
  }

  /**
   * Test the complete ranked result.
   */
  @Test
  public void resultRankedAll() {
    EvaluationResult evalUnranked = evaluation.evaluate(result);
    EvaluationResult evalRanked = evaluation.evaluate(ranked);
    /*
     * Wenn wir alle nehmen, spielt das Ranking keine Rolle. Wir vergleichen Fliesskommazahlen mit
     * einem Delta (wegen der Präzisionsproblematik von Fliesskommazahlen, s. z.B.
     * http://en.wikipedia.org/wiki/Floating_point#Minimizing_the_effect_of_accuracy_problems):
     */
    final double delta = 1e-9;
    Assert.assertEquals("When evaluating all, ranking should not matter", evalRanked.f(),
        evalUnranked.f(), delta);
    System.out.println("Ranked, all: \n" + evalRanked);
  }

  /**
   * Test the top results of the ranked list.
   */
  @Test
  public void resultRankedTop() {
    EvaluationResult evalUnranked = evaluation.evaluate(result.subList(0, K_START));
    EvaluationResult evalRanked = evaluation.evaluate(ranked.subList(0, K_START));
    /*
     * Wenn das Ranking die Ergebnisse für die ersten k Dokumente nicht verbessert, stimmt etwas
     * nicht:
     */
    Assert.assertTrue(
        "Top k results of ranked list  should be better than top k results of unranked list",
        evalRanked.f() > evalUnranked.f());
    System.out.println("Ranked, top: \n" + evalRanked);
  }

  /**
   * Test result export to wiki markup.
   * @throws IOException If we can't write to the wiki output file location
   */
  @Test
  public void resultExport() throws IOException {
    /*
     * Für eine systematischere Aufbereitung von Ergebnissen schreiben wir unsere 8 Ergebnisse als
     * Tabellen in eine Textile Markup-Datei, hier komplett manuell und immer mit String.format,
     * damit alles auf einer Ebene ist und man sieht: wir können quasi unseren Ausgabetext
     * hinschreiben und über die Prozent-Syntax Werte einsetzen. Damit hat man eine sehr einfache
     * Form von Templating-Sprache, mit der man relativ weit kommt.
     */
    final int k1 = 5, k2 = 7, k3 = 9, k4 = 10;
    EvaluationResult r1 = evaluation.evaluate(result.subList(0, k1));
    EvaluationResult r2 = evaluation.evaluate(result.subList(0, k2));
    EvaluationResult r3 = evaluation.evaluate(result.subList(0, k3));
    EvaluationResult r4 = evaluation.evaluate(result.subList(0, k4));
    EvaluationResult r5 = evaluation.evaluate(ranked.subList(0, k1));
    EvaluationResult r6 = evaluation.evaluate(ranked.subList(0, k2));
    EvaluationResult r7 = evaluation.evaluate(ranked.subList(0, k3));
    EvaluationResult r8 = evaluation.evaluate(ranked.subList(0, k4));
    FileWriter writer = new FileWriter(OUTPUT_TEXTILE);
    writer.write(String.format("h1. Unranked\n\n"));
    writer.write(String.format("table{ border: 1px solid; width:50%%; }.\n"));
    writer.write(String.format("| *k* | *p*  | *r*  | *f*  | \n"));
    writer.write(String.format("|  5  | %.2f | %.2f | %.2f | \n", r1.p(), r1.r(), r1.f()));
    writer.write(String.format("|  7  | %.2f | %.2f | %.2f | \n", r2.p(), r2.r(), r2.f()));
    writer.write(String.format("|  9  | %.2f | %.2f | %.2f | \n", r3.p(), r3.r(), r3.f()));
    writer.write(String.format("|  10 | %.2f | %.2f | %.2f | \n", r4.p(), r4.r(), r4.f()));
    writer.write(String.format("\nh1. Ranked\n\n"));
    writer.write(String.format("table{ border: 1px solid; width:50%%; }.\n"));
    writer.write(String.format("| *k* | *p*  | *r*  | *f*  | \n"));
    writer.write(String.format("|  5  | %.2f | %.2f | %.2f | \n", r5.p(), r5.r(), r5.f()));
    writer.write(String.format("|  7  | %.2f | %.2f | %.2f | \n", r6.p(), r6.r(), r6.f()));
    writer.write(String.format("|  9  | %.2f | %.2f | %.2f | \n", r7.p(), r7.r(), r7.f()));
    writer.write(String.format("|  10 | %.2f | %.2f | %.2f | \n", r8.p(), r8.r(), r8.f()));
    writer.close();
    /* Die ausgegebene Datei kann in Eclipse 3.5 (Galileo) als HTML etc. betrachtet werden. */
    System.out.println("Wrote wiki markup to: " + OUTPUT_TEXTILE);
  }

  /**
   * Test running of multiple k selections on the unranked result.
   */
  @Test
  public void multiResultsUnranked() {
    /*
     * Statt eine Tabelle wie oben manuell für die verschiedenen Aufbauten zu erstellen, ist in der
     * Praxis nützlicher, eine Art Experimentaufbau zu definieren, in der Art: 'Probiere alle k von
     * 5 bis 15 und gib die Ergebnisse aus' (hier einfach in der Konsole, doch stattdessen könnte
     * man die Wiki-Syntax von oben in eine Datei schreiben und so verschiedene Aufbauten in
     * verschiedenen Files speichern etc.).
     */
    String unrankedResults = formatted(result, K_START, K_END);
    Assert.assertTrue("Multi result string should exist and be longer than 0",
        unrankedResults != null && unrankedResults.length() > 0);
    System.out.println("Unranked: \n" + unrankedResults);
  }

  /**
   * Test running of multiple k selections on the ranked result.
   */
  @Test
  public void multiResultsRanked() {
    String rankedResults = formatted(ranked, K_START, K_END);
    Assert.assertTrue("Multi result string should exist and be longer than 0",
        rankedResults != null && rankedResults.length() > 0);
    System.out.println("Ranked: \n" + rankedResults);
  }

  private String formatted(final List<Document> list, final int kStart, final int kEnd) {
    StringBuilder builder = new StringBuilder();
    /* Für jedes k von kStart bis kEnd evaluieren wir und geben das Ergebnis aus: */
    for (int i = kStart; i <= kEnd; i++) {
      builder.append(evaluation.evaluate(list.subList(0, i)) + " k=" + i).append("\n");
    }
    return builder.toString().trim();

  }

}
