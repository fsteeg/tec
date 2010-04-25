package ir4;


/*
 * Interface zur Definition einer Strategie zur numerischen Repräsentation eines Vektorelements.
 * Ermöglicht Autauschbarkeit der Berechnung (z.B. cf statt df, oder auch komplett andere numerische
 * Werte als TF-IDF-artige Dinge).
 */
/**
 * Strategy interface for a numerical representation of a term.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface NumericalRepresentation {
  /**
   * @param term The term
   * @param document The document the term is a member of
   * @param index The index the document is a member of
   * @return A numerical representation of the given term
   */
  Float value(String term, Document document, InformationRetrieval index);

  /* TF-IDF-Berechnung als 'static member class', setzt im Wesentlichen die Formeln um. */
  /**
   * Member class implementing the interface with TF-IDF values.
   * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
   */
  public static final class TfIdf implements NumericalRepresentation {

    @Override
    public Float value(final String t, final Document d, final InformationRetrieval i) {
      return tfIdf(t, d, i);
    }

    private float tfIdf(final String t, final Document d, final InformationRetrieval i) {
      return tf(t, d) * idf(t, i);
    }

    private float tf(final String t, final Document d) {
      return d.getTf(t);
    }

    private float idf(final String t, final InformationRetrieval i) {
      return (float) Math.log(i.getWorks().size() / df(t, i));
    }

    private float df(final String t, final InformationRetrieval i) {
      return i.getDocumentFrequency(t);
    }
  };
}
