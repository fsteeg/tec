package ir5;

/*
 * Eine Klasse zur Darstellung der drei Elemente eines Evaluierungsergebnisses: Precision, Recall,
 * F-Maß.
 */
/**
 * Evaluation result consisting of precision, recall and f-measure.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class EvaluationResult {

  private float f;
  private float r;
  private float p;

  /**
   * @param p The precision
   * @param r The recall
   */
  public EvaluationResult(final float p, final float r) {
    this.p = p;
    this.r = r;
    /* Wir kapseln die F-Berechnung in der Klasse (statt F aussen zu berechnen und mit reinzugeben): */
    this.f = 2 * p * r / (p + r);
    /* Wenn f nicht zwischen 0 und 1 ist stimmt was nicht: */
    if (f < 0 || f > 1.0f) {
      throw new IllegalStateException("F should be between 0 and 1 but is: " + f);
    }
  }

  @Override
  public String toString() {
    /* In unserer toString-Darstellung formattieren wir die Zahlen auf zwei Nachkommastellen (%.2f) */
    return String.format("%s with p=%.2f, r=%.2f and f=%.2f", getClass().getSimpleName(), p, r, f);
  }

  /**
   * @return The f-measure
   */
  public float f() {
    return f;
  }

  /**
   * @return The recall
   */
  public float r() {
    return r;
  }

  /**
   * @return The precision
   */
  public float p() {
    return p;
  }

}
