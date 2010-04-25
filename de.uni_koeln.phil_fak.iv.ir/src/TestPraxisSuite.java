import ir1.TestPraxis1;
import ir2.TestPraxis2;
import ir3.TestPraxis3;
import ir4.TestPraxis4;
import ir5.TestPraxis5;
import ir6.TestPraxis6;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
 * Eine Suite ermöglicht das ausführen von mehreren Klassen mit Tests. Wird ausgefuehrt mit Run As
 * -> JUnit Test. Falls die JUnit-Bibliotheken nicht gefunden werden: Ctrl-1, Add JUnit 4 to the
 * Build Path.
 */
/**
 * Main test suite for the Information Retrieval exercises.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@RunWith( Suite.class )
@Suite.SuiteClasses( { TestPraxis1.class, TestPraxis2.class, TestPraxis3.class, TestPraxis4.class,
    TestPraxis5.class, TestPraxis6.class } )
public class TestPraxisSuite {}
