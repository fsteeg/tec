package ir1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
 * Eine Suite ermöglicht das ausführen von mehreren Klassen mit Tests. Wird ausgefuehrt mit Run As
 * -> JUnit Test. Falls die JUnit-Bibliotheken nicht gefunden werden: Ctrl-1, Add JUnit 4 to the
 * Build Path.
 */
/**
 * Suite for Information Retrieval exercise 1: linear search and term-document matrix.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@RunWith( Suite.class )
@Suite.SuiteClasses( { TestPraxis1Linear.class, TestPraxis1Matrix.class } )
public class TestPraxis1 {}
