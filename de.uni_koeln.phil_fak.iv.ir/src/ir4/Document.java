package ir4;

import ir2.Preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* Repräsentation eines Dokuments, das von einer Suchanfrage als Ergebnis geliefert wird. */
/**
 * Representation of the result we expect from information retrieval implementations: documents.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Document {
  /*
   * Konstanten, die die konkrete Implementierung einzelner Schitte bei der Arbeit mit Dokumenten
   * implementieren. Um sowas von aussen zu konfigurieren könnte man diese Instanzen z.B. im
   * Konstruktor übergeben. Eine andere Möglichkeit wäre, die Instanzen über einen Methodenausfruf
   * aus einer Template Method aufzurufen und Subklassen die konkreten Implementierungen liefern zu
   * lassen (vgl. Tests aus zweiter Praxissitzung).
   */
  private static final NumericalRepresentation NUMERICAL = new NumericalRepresentation.TfIdf();
  private static final VectorComparison COMPARISON = new VectorComparison.CosineSimilarity();
  private static final Preprocessor PREPROCESSOR = new Preprocessor();

  private String text;
  private String title;

  /*
   * Mapping der Termen zu ihren Häufigkeiten (term frequency tf, die Häufigkeit von Termen in
   * diesem Dokument):
   */
  private Map<String, Integer> terms;
  private List<String> tokens;

  /**
   * @param title The document title
   * @param text The document text
   */
  public Document(final String title, final String text) {
    this.text = text;
    this.title = title;
    this.terms = computeTf();
    this.tokens = PREPROCESSOR.tokenize(text);
  }
/*
   * Ein sog. Copy-Konstruktor: erzeugt eine inhaltsgleiche neue Instanz des Parameters, vgl. Bloch,
   * Item 11
   */
  /**
   * @param document The document to copy
   */
  public Document(final Document document) {
  /*
     * Wir rufen den anderen Konstruktor auf, der als zentraler Punkt fungiert, d.h. was dort
     * passiert passiert immer, egal welcher der beiden Konstruktoren aufgerufen wird (statt hier
     * die Sachen von oben doppelt zu machen):
     */
    this(document.title, document.text);
  }

  /**
   * @param that A document to compare this to
   * @param index The index
   * @return The similarity of this and that document, treating both as members of the given index
   */
  public Float similarity(final Document that, final InformationRetrieval index) {
    /* Die eigentliche Ähnlichkeitsberechnung delegieren wir an unsere Vergleichstrategie: */
    return COMPARISON.similarity(this.computeVector(index), that.computeVector(index));
  }

  /**
   * @param t A term
   * @return The term frequency of the given term, that is the number of occurrences of t in this
   *         document
   */
  public Integer getTf(final String t) {
    Integer integer = terms.get(t);
    return integer == null ? 0 : integer;
  }

  private Map<String, Integer> computeTf() {
    List<String> tokens = PREPROCESSOR.tokenize(text);
    Map<String, Integer> map = new HashMap<String, Integer>();
    /* Wir zählen die Häufigkeiten der Tokens: */
    for (String token : tokens) {
      Integer count = map.get(token);
      /*
       * Wenn der Term noch nicht vorkam, beginnen wir zu zählen (d.h. wir setzen 1), sonst zählen
       * wir hoch:
       */
      map.put(token, count == null ? 1 : count + 1);
    }
    return map;
  }

  private List<Float> computeVector(final InformationRetrieval index) {
    Set<String> terms = index.getTerms();
    /* Ein Vektor für dieses Dokument ist... */
    List<Float> vector = new ArrayList<Float>(terms.size());
    /* ...für jeden Term im Vokabular... */
    for (String t : terms) {
      /*
       * ...der numerische Wert des Terms (wir delegieren an die Berechnung der numerischen
       * Repräsentation oben):
       */
      vector.add(NUMERICAL.value(t, this, index));
    }
    return vector;
  }

  /**
   * @return The document title
   */
  public String getTitle() {
    return title;
  }
  
  /**
   * @return The document text
   */
  String getText() {
    return text;
  }

  /**
   * @return The document tokens
   */
  public List<String> getTokens() {
    return tokens;
  }

  /**
   * @return The document types
   */
  public Set<String> getTypes() {
    return terms.keySet();
  }

  @Override
  public String toString() {
    return title;
  }
  
  /* equals und hashCode, s. Bloch, Item 8 und 9 */

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Document)) {
      return false;
    }
    Document that = (Document) obj;
    /* Die relevanten Attribute unserer Klasse sind title und text, der Rest wir daraus berechnet: */
    return this.title.equals(that.getTitle()) && this.text.equals(that.getText());
  }

  @Override
  public int hashCode() {
    final int start = 17; // 17: some non-zero
    final int prime = 31;
    int result = start;
    /*
     * Die hashCode-Berechnung muss konsistent mit der equals-Methode sein, d.h. die gleichen
     * Attribute berücksichtigen:
     */
    result = prime * result + title.hashCode(); // 31: an odd prime
    result = prime * result + text.hashCode();
    return result;
  }

}
