package ir2;

import static org.junit.Assert.assertTrue;
import ir1.CorpusSimple;
import ir1.InformationRetrievalSimple;
import ir1.TestPraxis1Template;
import ir2.Preprocessor.ExtractionPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

/*
 * Inhalt der zweiten Übung: IR mit Listen und Intersection; reguläre Ausdrücke zur Vorverarbeitung
 * (Terme extrahieren, Rest dann simpel tokenisieren, dann in Types umwandeln).
 */
/**
 * Tests for Information Retrieval, exercise 2: inverted index and preprocessing.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class TestPraxis2 extends TestPraxis1Template {

  /* Wir klinken die neue Implementierung in unsere Tests vom letzten Mal ein: */
  @Override
  protected InformationRetrievalSimple getInformationRetrieval(final CorpusSimple corpus) {
    InvertedIndexSimple ir = new InvertedIndexSimple(corpus);
    System.out.println("Using InformationRetrieval implementation: "
        + ir.getClass().getSimpleName());
    return ir;
  }

  /*
   * Wir testen die Implementierungen der Schnittmengenbildung, die wir für die Verarbeitung von
   * Suchanfragen mit mehreren Wörtern baruachen:
   */
  private static final TreeSet<Integer> PL2 = new TreeSet<Integer>(Arrays.asList(2, 4, 6, 8));
  private static final TreeSet<Integer> PL1 = new TreeSet<Integer>(Arrays.asList(4, 3, 2, 1));
  private static final List<Integer> EXPECTED = Arrays.asList(2, 4);

  /**
   * Tests the implementation of the intersection algorithm from the IR book (Manning et al.).
   */
  @Test
  public void intersectionBook() {
    System.out.println("Testing intersection, algorithm from Manning et al.");
    List<Integer> list = new ArrayList<Integer>(Intersection.BOOK.of(PL1, PL2));
    Assert.assertEquals(EXPECTED, list);
  }

  /**
   * Tests the simple intersection implementation based on the Java Collections API.
   */
  @Test
  public void intersectionApi() {
    System.out.println("Testing intersection, via Java API");
    List<Integer> list = new ArrayList<Integer>(Intersection.API.of(PL1, PL2));
    Assert.assertEquals(EXPECTED, list);
  }

  /* Wir testen die Vorverarbeitung anhand einiger konstruierter Beispiele: */

  /**
   * Test preprocessing for non-letter characters.
   */
  @Test
  public void preprocessNonLetter() {
    System.out.println("Testing preprocessing, non-letter characters");
    List<String> tokens = new Preprocessor().tokenize("test 123, 123 test, test 123");
    ArrayList<String> types = new ArrayList<String>(new TreeSet<String>(tokens));
    Assert.assertEquals(Arrays.asList("123", "test"), types);
  }

  /**
   * Test preprocessing of special patterns.
   */
  @Test
  public void preprocessSpecialCases() {
    System.out.println("Testing preprocessing, filter special cases");
    List<String> tokens = new Preprocessor().tokenize("test 0221-123123, 123 test, test 123");
    ArrayList<String> types = new ArrayList<String>(new TreeSet<String>(tokens));
    Assert.assertEquals(Arrays.asList("0221-123123", "123", "test"), types);
  }

  /**
   * Test preprocessing robustness for non-ASCII encodings and characters.
   */
  @Test
  public void preprocessEncoding() {
    System.out.println("Testing preprocessing, encoding issues");
    List<String> tokens = new Preprocessor().tokenize("test köln 12312, 123 test, test 123");
    ArrayList<String> types = new ArrayList<String>(new TreeSet<String>(tokens));
    Assert.assertEquals(Arrays.asList("123", "12312", "köln", "test"), types);
  }

  /**
   * Test special case patterns.
   */
  @Test
  /*
   * Einige Tests der für Ausdrücke für Telefonnummern, Uhrzeiten, Emails. Das hier sind quasi
   * low-level-Tests im Vergleich zu denen oben: wir testen nicht die Funktionalität der
   * Vorverarbeitung, sondern die regulären Ausdrücke in der ExtractionPattern-Enum.
   */
  public void patterns() {
    System.out.println("Testing preprocessing, regular expressions");
    /* Eine Telefonnummer soll matchen: */
    assertTrue("0221-470".matches(ExtractionPattern.COMPOUND_NUMBER.getRegex()));
    /* Aber auch nur eine Nummer, nicht mit allem drumherum: */
    assertTrue(!"Meine Nummer ist 0221-470.".matches(ExtractionPattern.COMPOUND_NUMBER.getRegex()));
    /* Und auch keine normale Nummer: */
    assertTrue(!"4711".matches(ExtractionPattern.COMPOUND_NUMBER.getRegex()));
    /* Und auch nichts ähnliches: */
    assertTrue(!"Daimler-Benz".matches(ExtractionPattern.COMPOUND_NUMBER.getRegex()));
    /* Ebenso Versionen, Uhrzeiten, Geldmengen: */
    assertTrue("8.04".matches(ExtractionPattern.COMPOUND_NUMBER.getRegex()));
    assertTrue("15:10".matches(ExtractionPattern.COMPOUND_NUMBER.getRegex()));
    assertTrue("3,50".matches(ExtractionPattern.COMPOUND_NUMBER.getRegex()));
    /* Einfache Mailadressen: */
    assertTrue("fabian.steeg@uni-koeln.de".matches(ExtractionPattern.EMAIL.getRegex()));
    /* Wenn auch sowas matchen soll wird es schwieriger: */
    assertTrue("fsteeg@spinfo.uni-koeln.de".matches(ExtractionPattern.EMAIL.getRegex()));
    /* Sachen die Mailadressen ähnlich sind sollen nicht matchen: */
    assertTrue(!"fabian@home".matches(ExtractionPattern.EMAIL.getRegex()));
  }
}
