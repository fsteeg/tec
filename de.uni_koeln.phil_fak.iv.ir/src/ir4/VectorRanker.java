package ir4;


import java.util.Comparator;

/*
 * Ein Comparator für Dokumente, der mit einem Anfragevektor instanziiert wird. Dieser ist die
 * Grundlage für den Vergleich: Die zwei zu vergleichenden Dokumente sollen auf Basis ihrer
 * Ähnlichkeit zu dem Anfragevektor verglichen werden. Der Comparator kann genutzt werden um
 * API-Methoden von Java mit unseren Dokumenten zu verwenden, z.B. Collections.sort(List,
 * Comparator), siehe Test-Klasse (TestPraxis4.java)
 */
/**
 * A comparator that ranks documents based on their similarity to a given document.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class VectorRanker implements Comparator<Document> {

  private Document query;
  private InformationRetrieval index;

  /**
   * @param doc The document to compare the documents to rank against
   * @param index The index the documents should be seen as members of
   */
  public VectorRanker(final Document doc, final InformationRetrieval index) {
    this.query = doc;
    this.index = index;
  }

  @Override
  public int compare(final Document d1, final Document d2) {
    /*
     * Wir sortieren alle Vektoren nach ihrer (Kosinus-) Ähnlichkeit zur Anfrage (query), dazu
     * ermitteln wir zunächst die Ähnlichkeiten von d1 zum Query und d2 zum Query:
     */
    Float s1 = d2.similarity(query, index);
    Float s2 = d1.similarity(query, index);
    /*
     * Und sortieren anschließend nach diesen beiden Ähnlichkeiten. Wir wollen absteigende
     * Ähnlichkeit, d.h. s2.compareTo(s1) statt s1.compareTo(s2) d.h. die höchsten Werte und damit
     * besten Treffer zuerst:
     */
    return s1.compareTo(s2);
  }

}
