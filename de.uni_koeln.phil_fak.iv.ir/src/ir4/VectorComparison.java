package ir4;

import java.util.List;

/*
 * Interface zur Definition einer Vektor-Vergleichsstrategie. Ermöglicht Autauschbarkeit der
 * Berechnung (z.B. euklidische Distanz statt Kosinusähnlichkeit)
 */
/**
 * Strategy interface for different comparison implementations.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public interface VectorComparison {

  /**
   * @param v1 The first vector
   * @param v2 The second vector
   * @return A value representing the similarity of the two given vectors
   */
  Float similarity(List<Float> v1, List<Float> v2);

  /* Als 'static member class' hier z.B. die Kosinusähnlichkeit: */
  /**
   * Computation of vector similary based on cosine.
   * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
   */
  public static final class CosineSimilarity implements VectorComparison {

    @Override
    public Float similarity(final List<Float> v1, final List<Float> v2) {
      /*
       * Bevor wir mit den Berechnung beginnen, prüfen wir ob das überhaupt funktionieren kann
       * (sowas erleichtert die Fehlersuche): die zu vergleichenden Vektoren müssen gleich lang
       * sein, sonst stimmt irgendwas überhaupt nicht:
       */
      if (v1.size() != v2.size()) {
        throw new IllegalArgumentException(String.format(
            "Cannot compare vector of legth %s with vector of length %s", v1.size(), v2.size()));
      }
      /*
       * Da die Winkel zwischen Vektoren in einem rein positiven Koordinatensystem maximal 90 Grad
       * betragen, ist die Kosinusähnlichkeit immer ein Wert zwischen 0 und 1 und so ein brauchbares
       * Maß zur Bestimmung der Ähnlichkeit (wobei 1 "identisch" und 0 "keine Ähnlichkeit" bedeutet)
       */
      float cosineSimilarity = cosineSimilarity(v1, v2);
      /*
       * Obiges behaupten und vermuten wir, aber sowas hier und da zu überprüfen macht die
       * Fehlersuche einfacher und erhöht das Vertrauen in die Korrektheit des Codes (wie auch oben
       * für die Eingangsbedingung):
       */
      if (cosineSimilarity < 0f || cosineSimilarity > 1f) {
        throw new IllegalStateException("Cosine similarity must be between 0 and 1, but is: "
            + cosineSimilarity);
      }
      return cosineSimilarity;
    }

    private float cosineSimilarity(final List<Float> v1, final List<Float> v2) {
      return dotProduct(v1, v2) / (euclidicLength(v1) * euclidicLength(v2));
    }

    private float dotProduct(final List<Float> v1, final List<Float> v2) {
      /* Dot product: ist die Summe der Produkte der korrespondierenden Vektor-Werte: */
      float sum = 0;
      for (int i = 0; i < v1.size(); i++) {
        sum += (v1.get(i) * v2.get(i));
      }
      return sum;
    }

    private float euclidicLength(final List<Float> v) {
      /* Euklidische Länge: Wurzel aus der Summe der quadrierten Elemente eines Vektors: */
      float sum = 0;
      for (int i = 0; i < v.size(); i++) {
        sum += Math.pow(v.get(i), 2);
      }
      return (float) Math.sqrt(sum);
    }

  };

}
