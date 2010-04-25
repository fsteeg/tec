package ir1;

/**
 * Search test, fills the slot in the template method of its superclass by returning a
 * {@link TermDocumentMatrix} implementation of the {@link InformationRetrievalSimple} interface.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class TestPraxis1Matrix extends TestPraxis1Template {

  @Override
  protected InformationRetrievalSimple getInformationRetrieval(final CorpusSimple corpus) {
    return new TermDocumentMatrix(corpus);
  }

}
