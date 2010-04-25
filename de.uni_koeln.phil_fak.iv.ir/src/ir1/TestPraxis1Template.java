package ir1;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * Tests zur Demonstration der Funktionalitaet aus der ersten Uebung: Korpus, lineare Suche und
 * Suche mit Term-Dokument-Matrix; dazu ein paar Laufzeitmessungen zur Verdeutlichung des
 * Unterschieds zwischen O(p * q), d.h. Textlänge mal Anzahl der Suchwörter, und O(1), d.h.
 * konstanter Laufzeit, die nach Aufbau der Matrix unabhaengig von der Textlaenge ist.
 */
/*
 * Diese Testklasse ist 'abstract' und die Tests selbst sind Template-Methoden (Vorlagen, die die
 * konkrete Implementierung des InformationRetreival-Interfaces ihren Subklassen überlassen). Mehr
 * zu diesem Vorgehen gibt es in Gamma et al, Design Patterns, S. 325.
 */
/**
 * Abstract superclass for tests of implementations of the {@link InformationRetrievalSimple} interface.
 * Defines template methods that are filled with the implementation to test by subclasses.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public abstract class TestPraxis1Template {

  private CorpusSimple corpus;

  /*
   * Die abstrakte Methode: muss von Subklassen implementiert werden und bestimmt die zu verwendende
   * Implementation.
   */
  /**
   * @param corpus The corpus to instantiate an {@link InformationRetrievalSimple} implementation for.
   * @return The implementation of the {@link InformationRetrievalSimple} interface to test
   */
  protected abstract InformationRetrievalSimple getInformationRetrieval(CorpusSimple corpus);

  /*
   * Die mit @BeforeClass annotierte Methode wird einmal vor allen Test-Methoden ausgefuehrt. Hier
   * kann man Sachen ausfuehren, die von allen Tests verwendet werden, hier eine Ausgbe um die
   * verschiedenen Tests zu unterscheiden.
   */
  /**
   * Separate individual tests visually by printing information.
   */
  @BeforeClass
  public static void before() {
    System.out.println("------------");
    System.out.println("New Test Run");
    System.out.println("------------");
  }

  /*
   * Die mit @Before annotierte Methode wird vor jedem Test (d.h. vor jeder Methode, die mit @Test
   * annotiert ist) ausgeführt. Der Gedanke dahinter ist dass man für jeden Test eine saubere, neue
   * Umgebung schafft, so dass die Tests sich nicht gegenseitig beeinflussen, sondern atomar und
   * unabhängig voneinander bleiben.
   */
  /**
   * Sets the corpus up.
   */
  @Before
  public final void setup() {
    /*
     * Wir uebergeben den Ort der Datei und das Muster, mit dem die Werke getrennt werden sollen, um
     * die Klasse etwas wiederverwertbarer zu halten:
     */
    corpus = new CorpusSimple("shaks12.txt", "1[56][0-9]{2}\n");
    /*
     * Oben die etwas komplexere Version die wir im Seminar nur angesprochen haben. Der Vorteil ist
     * dass das Werk den Title enthält ('by William Shakespeare' kommt immer nach dem Titel). Der
     * reguläre Ausdrcuk den wir hier verwenden lässt sich lesen als: eine 1, dann eine 5 oder eine
     * 6, dann etwas von 0 bis 9, zwei mal, dann ein newline (was wir beim Einlesen dann auch
     * bewahren müssen). Mehr zu regulären Ausdrücken (ein sehr vielseitiges Werkzeug) findet sich
     * in Friedl, Mastering Regular Expressions 2nd Ed.
     */
  }

  /**
   * Test corpus creation.
   */
  @Test
  public final void testCorpus() {
    Assert.assertTrue("Corpus should exist", corpus.getWorks().size() > 0);
  }

  /**
   * Test searching the corpus for a single term.
   */
  @Test
  public final void testSearch() {
    Set<Integer> list = getInformationRetrieval(corpus).search("Brutus");
    System.out.println(list);
    Assert.assertTrue("Search should find a single term", list.size() > 0);
  }

  /**
   * Test searching the corpus for multiple search terms.
   */
  @Test
  public final void testMulti() {
    Set<Integer> list = getInformationRetrieval(corpus).search("Brutus Caesar");
    System.out.println(list);
    Assert.assertTrue("Search should find multiple terms", list.size() > 0);
  }
  /*
   * Was man ausserdem so testen könnte: Ob die Suche nach einem Term eine Ergebnis liefert, das
   * auch in dem Ergebnis für den Term und einen weiteren Term enthalten ist (z.B.
   * 'Brutus'-Ergebnisse sollten in 'Brutus Caesar'-Ergebnissen enthalten sein); ausserdem sollten
   * die zwei Implementierung gleiche Ergebnisse liefern, dafür könnten wir einen separaten Test
   * schreiben, etc.
   */
}
