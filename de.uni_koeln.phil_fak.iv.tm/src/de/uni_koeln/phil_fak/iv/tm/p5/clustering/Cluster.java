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
package de.uni_koeln.phil_fak.iv.tm.p5.clustering;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/**
 * A cluster of documents.
 * @author Fabian Steeg (fsteeg)
 */
public final class Cluster implements Iterable<Document> {
    List<Document> documents = new CopyOnWriteArrayList<Document>();
    private Document medoid = null;
    private Corpus corpus;

    public Cluster(final Corpus corpus, final Document document) {
        this.corpus = corpus;
        this.medoid = document;
        this.documents.add(document);
    }

    /**
     * @return The medoid of this cluster, i.e. the document with the highest
     *         similarity to the other documents, the most central member of the
     *         cluster in the vector space
     */
    public Document getMedoid() {
        return medoid;
    }

    /*
     * Dadurch dass wir Iterable implementieren können wir unsere Cluster in
     * einer foreach-Schleife verwenden: for(Document doc : cluster){ ... }
     */
    /**
     * {@inheritDoc}
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Document> iterator() {
        return documents.iterator();
    }

    /**
     * @param cluster The cluster to label
     * @param corpus The corpus the document belong to
     * @return A label for the given cluster
     */
    public String getLabel() {
        /*
         * Eine einfache Heuristik: Das Label ist das topic des Medoids, auf
         * ungefähr gleiche Länge getrimmt:
         */
        String label = medoid.getTopic();
        label = label.substring(0, Math.min(8, label.length()));
        return label.toUpperCase();
    }

    /**
     * @return The documents of this cluster
     */
    public List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    /*
     * Sog. package-private API (in Java die default-Sichtbarkeit), d.h. nur vom
     * eigenen Package aus sichtbar. Das documents-Attribut ist hier package
     * private. Die Manipulation eines clusters ist so nur innerhalb des Package
     * möglich. Der Cluster der als Ergebnis zurückgegeben wird ist so nicht
     * mehr manipulierbar. Die Veränderung des Clusters ist so hinter der public
     * API weggekapselt.
     */

    /**
     * Recompute the medoid of this cluster based on its members
     */
    void recomputeMedoid() {
        /*
         * Der Schwerpunkt eines Clusters: Das Element mit der geringsten
         * durchschnittlichen Entfernung, d.h. der größten Ähnlichkeit, zu allen
         * anderen Elementen im Cluster.
         */
        Float maxMean = Float.NEGATIVE_INFINITY;
        int maxIndex = -1;
        for (int i = 0; i < documents.size(); i++) {
            Float simSum = 0f;
            for (int j = 0; j < documents.size(); j++) {
                /*
                 * i ist der aktuelle Kandidat: wir messen die Ähnlichkeit von i
                 * zu allen j, der i mit der höchsten durchschnittlichen
                 * Ähnlichkeit gewinnt.
                 */
                Document iDoc = documents.get(i);
                Document jDoc = documents.get(j);
                if (!iDoc.equals(jDoc)) {
                    /* jeder Paar i,j Ähnlichkeit berechnen: */
                    Float sim = iDoc.getVector(corpus).similarity(jDoc.getVector(corpus));
                    /* Ähnlichkeit aufsummieren: */
                    simSum += sim;
                }
            }
            /* Die durchschnittliche Ähnlichkeit von i zu allen anderen: */
            Float meanSim = simSum / getDocuments().size() - 1;
            if (meanSim > maxMean) {
                maxMean = meanSim;
                /* i ist der beste Schwerpunkt bisher: */
                maxIndex = i;
            }
        }
        if (maxIndex != -1) {
            /*
             * Hier fehlte im Eifer der schon überzogenen letzten Minuten im
             * Seminar das kleine Wörtchen "return", so dass trotz Ergebnis
             * immer null zurückgegeben wurde, ansonsten war es soweit komplett.
             */
            this.medoid = documents.get(maxIndex);
        } else {
            throw new IllegalStateException("No max found!");
        }
    }
}
