/**
 * Material for the course 'Text-Mining', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html)
 * <p/>
 * Copyright (C) 2008-2009 Fabian Steeg
 * <p/>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_koeln.phil_fak.iv.tm.p3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_koeln.phil_fak.iv.ir.p6.crawler.Crawler;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.CorpusDatabase;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;
import de.uni_koeln.phil_fak.iv.tm.p3.classification.NaiveBayes;
import de.uni_koeln.phil_fak.iv.tm.p3.classification.TextClassifier;

/*
 * Text-Mining (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html),
 * dritte Praxissitzung (tm.p3). Fachlich: Textklassifikation mit Naive Bayes.
 * Technisch: Delegation und Strategie für austauschbare
 * Klassifikationsverfahren (wird nächste Sitzung ausgebaut).
 */
/**
 * Material for the course 'Text-Mining', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html)
 * @author Fabian Steeg (fsteeg)
 */
public class Praxis3 {
    private static final String DATA = "data/corpus-tm-3.db";
    private Corpus corpus;
    private List<Document> testingSet;
    private List<Document> trainingSet;
    private ArrayList<Document> goldSet;
    private TextClassifier textClassifier;

    public static void main(final String[] args) {
        /* Hier (= Run as -> Java application) erstellen und crawlen (dauert). */
        Corpus c = CorpusDatabase.create(DATA);
        List<Document> documents = Crawler.crawl(1, "http://www.spiegel.de",
                "http://www.bild.de");
        c.addAll(documents);
    }

    @Before
    public void before() {
        /* Hier (vor jedem Test) nur öffnen. */
        corpus = CorpusDatabase.open(DATA);
        System.out.println("------------------------------------------------");
    }

    @Test
    public void bild() {
        /*
         * Für unser Beispiel hier trainieren und klassifizieren wir mit den
         * gleichen Dokumenten. Dies ist eine übliche erste Evaluierung von
         * Klassifikationsverfahren. Wenn wir für unser Szenarion gemischte
         * Quellen verwenden wollen (z.B. Training mit Bild, Test mit Spiegel,
         * müssten wir uns darum kümmern die Klassen aufeinander zu mappen um
         * sinnvolle Ergebnisse zu bekommen)
         */
        String query = "bild";
        trainingSet = corpus.getDocumentsForSource(query);
        testingSet = corpus.getDocumentsForSource(query);
        testEval(query);

    }

    @Test
    public void spiegel() {
        String query = "spiegel";
        trainingSet = corpus.getDocumentsForSource(query);
        testingSet = corpus.getDocumentsForSource(query);
        testEval(query);

    }

    private void testEval(final String query) {
        goldSet = new ArrayList<Document>(testingSet);
        textClassifier = new TextClassifier(new NaiveBayes(), trainingSet);
        System.out.println("Classification of documents from: " + query);
        System.out.println("------------------------------------------------");
        System.out.println("Training set: " + trainingSet.size());
        System.out.println("Testing set: " + testingSet.size());
        System.out.println("Gold set: " + goldSet.size());
        Map<Document, String> resultClasses = textClassifier.classify(testingSet);
        Float result = textClassifier.evaluate(resultClasses, goldSet);
        System.out.println("Result: " + resultClasses);
        Assert.assertTrue("Result must not be null", result != null);
        System.out.println(String.format("Correct: %1.2f (%1.2f%%)", result,
                result * 100));
    }

    @After
    public void after() {
        /* Hier (nach jedem Test) schliessen. */
        corpus.close();
    }
}
