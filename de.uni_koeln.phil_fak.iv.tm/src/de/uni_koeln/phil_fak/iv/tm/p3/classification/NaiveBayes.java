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
package de.uni_koeln.phil_fak.iv.tm.p3.classification;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/**
 * Naive bayes classifier strategy to use for text classification.
 * @author Fabian Steeg (fsteeg)
 */
public final class NaiveBayes implements ClassifierStrategy {

    /** Number of documents for each class */
    private Map<String, Integer> classFrequencies = new HashMap<String, Integer>();
    /**
     * For each class, we map a mapping of all the terms of that class to their
     * term frequencies:
     */
    private Map<String, Map<String, Integer>> termFrequenciesForClasses = new HashMap<String, Map<String, Integer>>();
    private int docCount = 0;

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p3.classification.ClassifierStrategy#train(de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document,
     *      java.lang.String)
     */
    public ClassifierStrategy train(final Document doc, final String c) {
        /*
         * Wir zählen mit, wie viele Dokumente wir insgesamt haben, für die
         * Berechnung der A-Priori-Wahrscheinlichkeit ('prior probability')
         */
        docCount++;
        Integer classCount = classFrequencies.get(c);
        if (classCount == null) {
            /* Erstes Vorkommen der Klasse: */
            classCount = 0;
        }
        classFrequencies.put(c, classCount + 1);
        /*
         * Für die Evidenz: Häufigkeit eines Terms in den Dokumenten einer
         * Klasse.
         */
        Map<String, Integer> termCount = termFrequenciesForClasses.get(c);
        if (termCount == null) {
            /* Erstes Vorkommen der Klasse: */
            termCount = new HashMap<String, Integer>();
        }
        /* Jetzt für jeden Term hochzählen: */
        for (String term : doc.getTerms()) {
            Integer count = termCount.get(term);
            if (count == null) {
                /* Erstes Vorkommen des Terms: */
                count = 0;
            }
            /*
             * Wir addieren hier nicht einfach 1 wie ich es fälschlicherweise im
             * Seminar gemacht habe, sondern die Häufigkeit des Terms im
             * Dokument. Diese Zahl bekommen wir aus dem Dokument, weshalb wir
             * die Trennung doch nicht so machen können wie ich es vorhatte. So
             * kennt die Classifier-Strategie die Dokumentenklasse. Das ist
             * nicht weiter schlimm, weil die verschiedenen Strategien trotzdem
             * austauschbar sind. Sie können nur nicht mit einer anderen Klasse
             * als Document-Implementierungen zusammenarbeiten.
             */
            termCount.put(term, count + doc.getTermFrequencyOf(term));
        }
        termFrequenciesForClasses.put(c, termCount);
        return this;
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p3.classification.ClassifierStrategy#classify(de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document)
     */
    public String classify(final Document doc) {
        /* Das Maximum... */
        float max = Float.NEGATIVE_INFINITY;
        Set<String> classes = termFrequenciesForClasses.keySet();
        String best = classes.iterator().next();
        /* ...der möglichen Klassen... */
        for (String c : classes) {
            /*
             * Das Produkt oder die Summe der Termwahrscheinlichkeiten ist
             * unsere Evidenz...
             */
            float evidence = 0f;
            /*
             * Hier lag das Problem: Wir sind hier im Seminar absurderweise über
             * alle Terme für die Klasse die wir hier betrachten gelaufen, statt
             * nur über die Terme in dem Dokument das wir klassifizieren wollen.
             */
            for (String term : doc.getTerms()) {
                float e = evidence(term, c);
                evidence = (float) (evidence * Math.log(e));
            }
            float prior = prior(c);
            /* Die eigentliche Naive-Bayes Berechnung: */
            float probability = (float) (prior + evidence);
            /* Und davon das Maximum: */
            if (probability >= max) {
                max = probability;
                best = c;
            }
        }
        return best;
    }

    private float prior(final String c) {
        /* The relative frequency of the class: */
        Integer classCount = classFrequencies.get(c);
        float prior = (float) Math.log(classCount / (float) docCount);
        return prior;
    }

    private float evidence(final String term, final String c) {
        Map<String, Integer> termFreqsForClass = termFrequenciesForClasses
                .get(c);
        Integer termFrequency = termFreqsForClass.get(term);
        float evidence;
        /*
         * Dieser Test fehlte auch noch: wenn ein Term in den
         * Trainingsdokumenten für die Klasse nicht vorkommt, ist die Evidenz
         * unendlich klein (weil wir damit vergleiche bei der Suche nach dem
         * Maximum):
         */
        if (termFrequency != null) {
            evidence = termFrequency / (float) sum(termFreqsForClass);
        } else {
            evidence = Float.NEGATIVE_INFINITY;
        }
        return evidence;
    }

    /* Die Summe der Häufigkeiten der Termfrequenzen für die Klasse: */
    private float sum(final Map<String, Integer> termFreqsForClass) {
        int sum = 0;
        for (Integer i : termFreqsForClass.values()) {
            sum += i;
        }
        return sum;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
    
}
