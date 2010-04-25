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
package de.uni_koeln.phil_fak.iv.tm.p4.classification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;
import de.uni_koeln.phil_fak.iv.tm.p3.classification.ClassifierStrategy;

/**
 * Adapter for Weka classifiers.
 * @author Fabian Steeg (fsteeg)
 */
public final class WekaAdapter implements ClassifierStrategy {

    private Classifier wekaClassifier;
    private int featureCount;
    private Set<String> classes;
    private Instances trainingSet;
    private Corpus corpus;

    /**
     * @param wekaClassifier The Weka classifier to adapt
     * @param trainingData The training documents
     * @param corpus The corpus
     */
    public WekaAdapter(final Classifier wekaClassifier,
            final List<Document> trainingData, final Corpus corpus) {
        this.wekaClassifier = wekaClassifier;
        this.corpus = corpus;
        this.featureCount = trainingData.get(0).getVector(corpus).getValues()
                .size();
        this.classes = collectClasses(trainingData);
        this.trainingSet = initTrainingSet(trainingData);
        train(trainingData);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p3.ClassifierStrategy#train(de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document,
     *      java.lang.String)
     */
    public ClassifierStrategy train(final Document document,
            final String classLabel) {
        trainingSet.add(instanceFor(document, classLabel));
        return this;
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p3.ClassifierStrategy#classify(de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document)
     */
    public String classify(final Document document) {
        /* Eine unklassifizierte Instanz soll klassifiziert werden: */
        Instance instance = instanceFor(document, null);
        try {
            /*
             * Ein Array mit der Wahrscheinlichkeitsverteilung der Klassen,
             * korrespondiert mit den möglichen Klassen des Klassifikators:
             */
            double[] distribution = wekaClassifier
                    .distributionForInstance(instance);
            /*
             * D.h. die korrespondierende Klasse mit dem höchsten Wert ist die
             * beste Klasse für die Merkmale:
             */
            int maxIndex = findMaxIndex(distribution);
            return new ArrayList<String>(classes).get(maxIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        /*
         * toString on the Weka classifier is quite useful but not really
         * concise in every case, so we use the name only here:
         */
        return String.format("Weka: %s", this.wekaClassifier.getClass()
                .getSimpleName());
    }

    private Instances initTrainingSet(final List<Document> trainingData) {
        /* Der FastVector enthält die Merkmale: */
        FastVector instanceStructure = new FastVector(featureCount + 1);
        /* Die Klasse wird in Weka auch als Merkmalsvektor dargestellt: */
        FastVector possibleClasses = new FastVector(classes.size());
        for (String className : classes) {
            /*
             * Die Klasse ist nicht numerisch, deshalb muessen alle möglichen
             * Werte angegeben werden:
             */
            possibleClasses.addElement(className);
        }
        /* An Stelle 0 unseres Gesamtvektors kommt der Klassen-Vektor: */
        instanceStructure.addElement(new Attribute("Ressort", possibleClasses));
        for (int i = 0; i < featureCount; i++) {
            /*
             * An jeder Position unseres Merkmalsvektors haben wir ein
             * numerisches Merkmal (repräsentiert als Attribute), dessen Name
             * sein Index ist:
             */
            instanceStructure.addElement(new Attribute(i + ""));
        }
        /*
         * Schliesslich erstellen wir einen Container für unsere
         * Trainingsbeispiele, der Instanzen der beschriebenen Merkmale
         * enthalten wird:
         */
        Instances result = new Instances("InstanceStructure",
                instanceStructure, 1);
        /*
         * Wobei wir noch angeben muessen, an welcher Stelle der
         * Merkmalsvektoren die Klasse zu finden ist:
         */
        /*
         * Diese Zeile fehlte im Seminar, deshalb lief es nicht.
         */
        result.setClassIndex(0);
        return result;
    }

    private void train(final List<Document> trainingData) {
        for (Document document : trainingData) {
            train(document, document.getTopic());
        }
        try {
            wekaClassifier.buildClassifier(trainingSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> collectClasses(final List<Document> trainingData) {
        Set<String> classes = new HashSet<String>();
        for (Document document : trainingData) {
            classes.add(document.getTopic());
        }
        return classes;
    }

    private int findMaxIndex(final double[] distribution) {
        int maxIndex = -1;
        double max = Double.MIN_VALUE;
        for (int j = 0; j < distribution.length; j++) {
            double current = distribution[j];
            if (current > max) {
                max = current;
                maxIndex = j;
            }
        }
        return maxIndex;
    }

    private Instance instanceFor(final Document document,
            final String classLabel) {
        /* Die Instanz enthält alle Merkmale plus die Klasse: */
        Instance instance = new Instance(featureCount + 1);
        List<Float> features = document.getVector(corpus).getValues();
        for (int i = 0; i < featureCount; i++) {
            instance.setValue(i + 1, features.get(i));
        }
        /*
         * Und muss erfahren, was die Werte bedeuten, was wir für unser
         * Trainingsset beschrieben hatten:
         */
        instance.setDataset(trainingSet);
        /*
         * Beim Training haben wir Instanzen mit vorhandenem Klassenlabel, bei
         * der Klassifikation ist die Klasse unbekannt:
         */
        if (classLabel == null) {// during classification
            instance.setClassMissing();
        } else { // during training
            instance.setClassValue(classLabel);
        }
        return instance;
    }

}
