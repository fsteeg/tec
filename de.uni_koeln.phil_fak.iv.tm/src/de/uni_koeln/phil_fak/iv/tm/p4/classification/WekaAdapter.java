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
import de.uni_koeln.phil_fak.iv.ir.p5.features.FeatureVector;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;
import de.uni_koeln.phil_fak.iv.tm.p3.classification.ClassifierStrategy;

/**
 * Adapter for Weka classifiers.
 * @author Fabian Steeg (fsteeg)
 */
public class WekaAdapter implements ClassifierStrategy {

    private Classifier wekaClassifier;
    private int vectorSize;
    private List<String> classes;
    private Instances trainingSet;
    private Corpus corpus;
    private boolean classifierBuilt = false;

    /**
     * @param wekaClassifier The Weka classifier to adapt
     * @param trainingData The training documents
     * @param corpus The corpus
     */
    public WekaAdapter(Classifier wekaClassifier,
            Set<Document> trainingData, Corpus corpus) {
        this.wekaClassifier = wekaClassifier;
        this.corpus = corpus;
        // Fuer Weka brauchen wir jetzt ein paar Sachen:
        // 1. Die Groesse des Merkmalsvektors:
        FeatureVector vector = trainingData.iterator().next().getVector(corpus);
        this.vectorSize = vector.getValues().size();
        // 2. Die moegliche Klassen:
        this.classes = collectClasses(trainingData);
        // 3. Die Struktur der Trainingsdaten
        this.trainingSet = initTraininSet(trainingData);
    }
    
    private List<String> collectClasses(Set<Document> trainingData) {
        Set<String> classes = new HashSet<String>();
        for (Document document : trainingData) {
            classes.add(document.getTopic());
        }
        return new ArrayList<String>(classes);
    }

    private Instances initTraininSet(Set<Document> trainingData) {
        /* Der FastVector enthält die Merkmale: */
        FastVector structureVector = new FastVector(vectorSize + 1);
        /* Die Klasse wird in Weka auch als Merkmalsvektor dargestellt: */
        FastVector classesVector = new FastVector(this.classes.size());
        for (String c : classes) {
          /*
           * Die Klasse ist nicht numerisch, deshalb muessen alle möglichen
           * Werte angegeben werden:
           */
            classesVector.addElement(c);
        }
        /* An Stelle 0 unseres Gesamtvektors kommt der Klassen-Vektor: */
        structureVector.addElement(new Attribute("Ressort", classesVector));
        for (int i = 0; i < vectorSize; i++) {
          /*
           * An jeder Position unseres Merkmalsvektors haben wir ein
           * numerisches Merkmal (repräsentiert als Attribute), dessen Name
           * sein Index ist:
           */
            structureVector.addElement(new Attribute(i + "")); // Merkmal i,
                                                               // d.h. was? >
                                                               // TF-IDF
        }
        /*
         * Schliesslich erstellen wir einen Container für unsere
         * Trainingsbeispiele, der Instanzen der beschriebenen Merkmale
         * enthalten wird:
         */
        Instances result = new Instances("InstanceStructure", structureVector,
                vectorSize + 1);
        /*
         * Wobei wir noch angeben muessen, an welcher Stelle der
         * Merkmalsvektoren die Klasse zu finden ist:
         */
        result.setClassIndex(0);
        return result;
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p3.ClassifierStrategy#train(de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document,
     *      java.lang.String)
     */
    @Override public ClassifierStrategy train(Document document, String label) {
        trainingSet.add(instance(document, label));
        classifierBuilt = false;
        return this;
    }
    
    private Instance instance(Document document, String label) {
        List<Float> values = document.getVector(corpus).getValues();
        /* Die Instanz enthält alle Merkmale plus die Klasse: */
        double[] vals = new double[values.size() + 1];
        for (int i = 0; i < values.size(); i++) {
            vals[i + 1] = values.get(i);
        }
        Instance instance = new Instance(1, vals);
        /*
         * Und muss erfahren, was die Werte bedeuten, was wir für unser
         * Trainingsset beschrieben hatten:
         */
        instance.setDataset(trainingSet);
        /*
         * Beim Training haben wir Instanzen mit vorhandenem Klassenlabel, bei
         * der Klassifikation ist die Klasse unbekannt:
         */
        if (label == null) {
            instance.setClassMissing(); // during classification
        } else
            instance.setClassValue(label); // during training
        return instance;
    }
    
    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p3.ClassifierStrategy#classify(de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document)
     */
    @Override public String classify(Document document) {
        if (!classifierBuilt) {
            try {
                wekaClassifier.buildClassifier(trainingSet);
                classifierBuilt = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Instance instance = instance(document, null);
            int i = (int) wekaClassifier.classifyInstance(instance);
            // double[] distribution =
            // wekaClassifier.distributionForInstance(instance); < Alternative
            return classes.get(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return String.format("%s for %s", getClass().getSimpleName(),
                wekaClassifier.getClass().getSimpleName());
    }

}
