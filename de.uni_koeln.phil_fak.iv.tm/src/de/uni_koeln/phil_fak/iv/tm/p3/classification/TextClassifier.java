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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/**
 * Text classification delegating the actual classification to a classifier
 * strategy.
 * @author Fabian Steeg (fsteeg)
 */
public final class TextClassifier {

    private ClassifierStrategy classifier;

    /**
     * @param classifier The classifier strategy to use for text classification
     * @param trainingSet The training set for this classifier
     */
    public TextClassifier(final ClassifierStrategy classifier,
            final Set<Document> trainingSet) {
        this.classifier = classifier;
        train(trainingSet);
    }

    private void train(final Set<Document> trainingDocuments) {
        /* Wir trainieren mit jedem Dokument: */
        for (Document document : trainingDocuments) {
            /* Delegieren das eigentliche Training an unsere Strategie: */
            this.classifier = classifier.train(document, document.getTopic());
        }
    }

    /**
     * @param documents The documents to classify
     * @return A mapping of documents to their class labels
     */
    public Map<Document, String> classify(final Set<Document> documents) {
        Map<Document, String> resultClasses = new HashMap<Document, String>();
        for (Document document : documents) {
            /* Wie beim Training delegieren wir an die Strategie: */
            String classLabel = classifier.classify(document);
            /*
             * Und speichern wie im Seminar vorgeschlagen wurde, die Ergebnisse
             * in einer Map, um die fehleranfälligen korrespondierenden Listen
             * zu vermeiden:
             */
            resultClasses.put(document, classLabel);
        }
        return resultClasses;
    }

    /**
     * @param resultClasses The classification result
     * @param gold The gold standard
     * @return The ration of correct labels in classified, according to the gold
     */
    public Float evaluate(final Map<Document, String> resultClasses,
            final ArrayList<Document> gold) {
        /* Wir zählen die Anzahl der Übereinstimmungen: */
        int same = 0;
        for (Document document : gold) {
            String classLabel = resultClasses.get(document);
            if (classLabel.equalsIgnoreCase(document.getTopic())) {
                same++;
            }
        }
        /* Und berechnen daraus den Anteil korrekter Werte: */
        return same / (float) gold.size();
        /*
         * Eigentlich mit Annotationen evaluieren (als generisches
         * Austauschformat), aber für die Übersichtlichkeit hier so.
         */
    }
}
