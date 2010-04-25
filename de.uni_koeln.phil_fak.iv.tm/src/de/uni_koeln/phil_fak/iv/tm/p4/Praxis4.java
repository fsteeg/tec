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
package de.uni_koeln.phil_fak.iv.tm.p4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.misc.HyperPipes;
import de.uni_koeln.phil_fak.iv.ir.p6.crawler.Crawler;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.CorpusDatabase;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;
import de.uni_koeln.phil_fak.iv.tm.p3.classification.ClassifierStrategy;
import de.uni_koeln.phil_fak.iv.tm.p3.classification.NaiveBayes;
import de.uni_koeln.phil_fak.iv.tm.p3.classification.TextClassifier;
import de.uni_koeln.phil_fak.iv.tm.p4.classification.WekaAdapter;

/*
 * Text-Mining (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html),
 * vierte Praxissitzung (tm.p4). Fachlich: Vergleichende Textklassifikation mit
 * verschiedenen Klassifikationsverfahren. Technisch: Delegation und Strategie
 * für austauschbare Klassifikationsverfahren; Nutzung der Weka-API.
 */
/**
 * Material for the course 'Text-Mining', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html)
 * @author Fabian Steeg (fsteeg)
 */
public class Praxis4 {
    private static final String LINE = "------------------------------------------------------------------------";
    private static final String DATA = "data/corpus-tm-4.db";
    private Corpus corpus;
    private List<Document> testingSet;
    private List<Document> trainingSet;
    private ArrayList<Document> goldSet;
    private TextClassifier textClassifier;

    public static void main(final String[] args) {
        /* Hier (= Run as -> Java application) erstellen und crawlen (dauert). */
        Corpus c = CorpusDatabase.create(DATA);
        List<Document> documents = Crawler.crawl(1, 50, "http://www.spiegel.de",
                "http://www.bild.de");
        c.addAll(documents);
    }

    @Before
    public void before() {
        /* Hier (vor jedem Test) nur öffnen. */
        corpus = CorpusDatabase.open(DATA);
        System.out.println(LINE);
    }

    @Test
    public void bild() {
        testWith("bild");
    }

    @Test
    public void spiegel() {
        testWith("spiegel");
    }

    @After
    public void after() {
        /* Hier (nach jedem Test) schliessen. */
        corpus.close();
        System.out.println();
    }

    private void testWith(final String query) {
        setupData(query);
        printInfo(query);
        /*
         * Wir übergeben hier die Startzeit um die Trainingsphase mitzumessen;
         * das Training findet im Konstrukltor statt und war so nicht mit in der
         * Messung. So sieht man auch dass der SVM-Klassifikator immer etwa
         * doppelt so lange dauert wie die anderen hier.
         */
        testEval(System.nanoTime(), query, new NaiveBayes());
        testEval(System.nanoTime(), query, new WekaAdapter(
                new weka.classifiers.bayes.NaiveBayes(), trainingSet, corpus));
        testEval(System.nanoTime(), query, new WekaAdapter(new IBk(),
                trainingSet, corpus));
        testEval(System.nanoTime(), query, new WekaAdapter(new SMO(),
                trainingSet, corpus));
        testEval(System.nanoTime(), query, new WekaAdapter(new HyperPipes(),
                trainingSet, corpus));
    }

    private void setupData(final String query) {
        /*
         * Für unser Beispiel hier trainieren und klassifizieren wir mit den
         * gleichen Dokumenten. Dies ist eine übliche erste Evaluierung von
         * Klassifikationsverfahren. Wenn wir für unser Szenarion gemischte
         * Quellen verwenden wollen (z.B. Training mit Bild, Test mit Spiegel,
         * müssten wir uns darum kümmern die Klassen aufeinander zu mappen um
         * sinnvolle Ergebnisse zu bekommen)
         */
        trainingSet = corpus.getDocumentsForSource(query);
        testingSet = trainingSet;
        goldSet = new ArrayList<Document>(testingSet);
    }

    private void testEval(final long start, final String query,
            final ClassifierStrategy classifier) {
        System.out.print(classifier + "... ");
        textClassifier = new TextClassifier(classifier, trainingSet);
        Map<Document, String> resultClasses = textClassifier
                .classify(testingSet);
        Float result = textClassifier.evaluate(resultClasses, goldSet);
        Assert.assertTrue("Result must not be null", result != null);
        long ns = System.nanoTime() - start;
        double ms = ns / 1000d / 1000d;
        double s = ms / 1000d;
        System.out.println(String.format(
                "Correct: %1.2f (%1.2f%%); Time: %1.2f ms (%1.2f s.)", result,
                result * 100, ms, s));
    }

    private void printInfo(final String query) {
        System.out.println("Classification of documents from: " + query
                + "... ");
        System.out.println(LINE);
        System.out.println("Training set: " + trainingSet.size());
        System.out.println("Testing set: " + testingSet.size());
        System.out.println("Gold set: " + goldSet.size());
        System.out.println(LINE);
    }
}
