package ir5;

import ir4.Document;

import java.util.List;

/*
 * Evaluation (Precision, Recall, F-Maß) eines Queries und einer Dokumentenmenge gegen einen
 * Goldstandard.
 */
/**
 * Simple evaluation of a list of documents against a gold standard.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Evaluation {

  private List<Document> relevant;

  /**
   * @param relevant The list of relevant or correct documents (i.e. the gold standard)
   */
  public Evaluation(final List<Document> relevant) {
    this.relevant = relevant;
  }

  /**
   * @param retrieved The list of documents to evaluate against the gold standard
   * @return The evaluation result for evaluating the given list of documents against the gold
   *         standard
   */
  public EvaluationResult evaluate(final List<Document> retrieved) {
    int tp = tp(retrieved, relevant);
    int fp = retrieved.size() - tp;
    int fn = relevant.size() - tp;
    float p = (float) tp / (tp + fp);
    float r = (float) tp / (tp + fn);
    return new EvaluationResult(p, r);
  }

  private int tp(final List<Document> retrieved, final List<Document> relevant) {
    int c = 0;
    for (Document document : retrieved) {
      /*
       * Zur Ermittlung der true positives zählen wir, wie viele der gefundenen auch relevant sind
       * (wir verwenden eine Copy-Constructor um die Situation zu simulieren, dass die Dokumente des
       * Goldstandards immer andere Instanzen sind als die Ergebnisse. Das wäre hier nicht nötig,
       * aber in zu ziemlich jeder praktischen Umsetzung eines kompletten Goldstandards):
       */
      if (relevant.contains(new Document(document))) {
        c++;
      }
    }
    return c;
    /* Die im Seminar angesprochene Alternative: */
    // List<Document> tp = new ArrayList<Document>(result); tp.retainAll(gold); return tp.size();
  }
}
