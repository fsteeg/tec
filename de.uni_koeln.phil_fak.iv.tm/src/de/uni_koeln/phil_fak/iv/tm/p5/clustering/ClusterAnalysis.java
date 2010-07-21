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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/*
 * Einfaches k-Means-Clustering über Tf-Idf und Kosinusähnlichkeit. Um die
 * Verfahren auszutauschen könnte man einen Weg gehen wie beim Klassifiziren:
 * ClusteringStrategy als Interface, eine Implementierung davon etwa KMeans, die
 * ClusteringStrategy über Interface hier nutzen.
 */
/**
 * Simple flat k-means clustering.
 * @author Fabian Steeg (fsteeg)
 */
public final class ClusterAnalysis {

    private Corpus corpus;
    private List<Cluster> clusters;
    private List<Document> documents;

    /**
     * K-Means clustering of the given documents, as part of the given corpus
     * @param corpus The corpus the document selection is a part of
     * @param documents The documents to cluster
     */
    public ClusterAnalysis(final Corpus corpus, final List<Document> documents) {
        this.corpus = corpus;
        /*
         * Da wir beim iterieren über die Cluster diese manipulieren (wir
         * verschieben die Elemente in den passendsten Cluster), muessen wir
         * eine List-Implementierung benutzen, die das unterstützt (die
         * Alternative wäre das Anlegen neuer Listen und sowas), wie etwa
         * CopyOnWriteArrayList, die (wie der umständliche Name sagt), das
         * zugrunde liegende Array bei Schreibe-Operationen kopiert, was sich
         * nachteilig auf die Performance auswirkt, wenn man nicht viel iteriert
         * und wenig schreibt:
         */
        clusters = new CopyOnWriteArrayList<Cluster>();
        this.documents = documents;
    }

    /**
     * Single clustering into k clusters.
     * @param k The number of clusters to partition the documents into
     * @param iterations The number of iterations
     * @return The k clusters
     */
    public List<Cluster> analyse(final int k, final int iterations) {
        // Initiale Mittelpunkte: k zufällige Dokumente
        Collections.shuffle(documents);
        for (int i = 0; i < k; i++) {
            /*
             * Ich bin hier dem Vorschlag aus dem Seminar gefolgt und habe für
             * einen Cluster eine eigene Klasse angelegt. Das hat neben der
             * Tatsache dass keine korrespondierenden Listen verwaltet werden
             * auch andere Vorteile, etwa können wir die Medoid-Berechnung und
             * das Labeln in die Klasse verlegen.
             */
            Cluster cluster = new Cluster(corpus, documents.get(i));
            clusters.add(cluster);
        }
        /* Zu Beginn bilden alle Dokumente einen Cluster: */
        clusters.get(0).documents.addAll(documents);
        System.out.println(String.format("%s-means clustering with %s iterations... ", k,
                iterations));
        for (int i = 0; i < iterations; i++) {
            /* In jeder iteration stellen wir die Cluster neu ein: */
            this.clusters = recompute(clusters);
            /* Eine simple Form von Fortschrittsanzeige: */
            System.out.print(String.format("%1.2f ", getPurity()));
        }
        System.out.println(String.format("Purity for k=%s: %1.2f, clusters: %s", k, getPurity(),
                toString()));
        System.out.println(toDot());
        return this.clusters;
    }

    private List<Cluster> recompute(final List<Cluster> clusters) {
        List<Cluster> result = new CopyOnWriteArrayList<Cluster>(clusters);
        /* Wir betrachten jedes Dokument in jedem Cluster: */
        for (Cluster currentCluster : result) {
            for (Document document : currentCluster) {
                /*
                 * Und suchen zu diesem den passendsten Cluster, d.h. den
                 * Cluster, dessen Schwerpunkt-Dokument dem Dokument am
                 * ähnlichsten, d.h. im Vektorraum am nächsten ist:
                 */
                Float max = Float.NEGATIVE_INFINITY;
                int bestIndex = -1;
                /* Ähnlichsten Mittelpunkt suchen: */
                for (int i = 0; i < clusters.size(); i++) {
                    Document center = clusters.get(i).getMedoid();
                    Float similarity =
                            document.getVector(corpus).similarity(center.getVector(corpus));
                    if (similarity > max) {
                        max = similarity;
                        bestIndex = i;
                    }
                }
                /* bestIndex ist der Index des ähnlichsten Mittelpunktes: */
                if (bestIndex != -1) {
                    /*
                     * Wenn ein solcher ähnlichster Schwerpunkt gefunden wurde,
                     * entfernen wir das aktuelle Dokument aus seinem, d.h.
                     * diesem Cluster, und fügen es in den Cluster ein, dessen
                     * Mittelpunkt gewonnen hatte:
                     */
                    currentCluster.documents.remove(document);
                    result.get(bestIndex).documents.add(document);
                    /*
                     * (und damit wir hier diese Sache so machen können müssen
                     * die Listen CopyOnWriteArrayList sein)
                     */
                }
            }
            /*
             * Nach der Neuberechnung des aktuellen Clusters setzen wir seinen
             * Medoid neu fest:
             */
            currentCluster.recomputeMedoid();
        }
        return result;
    }

    /**
     * Multiple clusterings for different values of k.
     * @param clusterStart The minimum k cluster count
     * @param clusterEnd The maximum k cluster count
     * @param iterations The number of iterations to use when clustering with
     *            each k
     * @return The clusters for each different k between clusterStart and
     *         clusterEnd (inclusive)
     */
    public List<List<Cluster>> analyse(final int clusterStart, final int clusterEnd,
            final int iterations) {
        final List<List<Cluster>> clustersForKs = new CopyOnWriteArrayList<List<Cluster>>();
        /*
         * Wir lassen die verschiedenen, völlig unabhängigen Versuchsaufbauten,
         * nämlich mit unterschiedlicher Clusterzahl (und leicht einbaubar
         * unterschiedlich vielen Iterationen) parallel laufen. Hier können wir
         * parallele Durchführung der verschiedenen Aufbauten einstellen. Ist
         * auf 1 gestellt um einfacher die Ausgabe interpretieren zu können und
         * fürs Debuggen. In Produktionacode würde man so einen Wert z.B. in
         * eine Properties-Datei aulagern.
         */
        ExecutorService exec = Executors.newFixedThreadPool(clusterEnd - clusterStart); // threads
        for (int i = clusterStart; i <= clusterEnd; i++) {
            final int k = i;
            exec.execute(new Runnable() {
                public void run() {
                    ClusterAnalysis c = new ClusterAnalysis(corpus, documents);
                    clusters = c.analyse(k, iterations);
                    /* Wir sammeln die Ergbeniss für jedes k: */
                    clustersForKs.add(clusters);
                }
            });
        }
        exec.shutdown();
        try {
            /** Basically no timeout */
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clustersForKs;
    }

    /**
     * @return The purity of the clusters
     */
    public Float getPurity() {
        /*
         * Die Purity ist die Anzahl der Elemente in jedem Cluster, die in
         * diesem Cluster am häufigsten vorkommen, geteilt durch die Anzahl
         * aller Elemente. So ist sie ein Mass für die Homogenität der Cluster.
         * Ein Wert von 1 heisst dabei maximale Purity: Jeder Cluster enthält
         * nur eine Art von Element.
         */
        int maxSum = 0;
        int sum = 0;
        for (Cluster currentCluster : clusters) {
            /*
             * Dazu müssen wir erstmal schauen, was denn die häufigste Art von
             * Element in jedem Cluster ist, d.h. wir betrachten zunächst jedes
             * Document im Cluster und zählen wie of es vorkommt:
             */
            Map<String, Integer> frequencies = new HashMap<String, Integer>();
            for (Document document : currentCluster) {
                Integer f = frequencies.get(document.getTopic());
                if (f == null) {
                    f = 0;
                }
                frequencies.put(document.getTopic(), f + 1);
            }
            /*
             * Dann schauen wir uns alle Häufigkeiten an und wählen die höchste:
             */
            Collection<Integer> values = frequencies.values();
            int max = 0;
            for (Integer integer : values) {
                max = Math.max(integer, max);
                /* Die Gesamtsumme zählen wir immer: */
                sum += integer;
            }
            /*
             * Zur Summe der maximalen Häufigkeiten addieren wir nur die
             * Häufigkeit des Häufigsten
             */
            maxSum += max;
        }
        Float result = maxSum / (float) sum;
        return result;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("|");
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            builder.append(String.format("%s:%s|", cluster.documents.size(), clusters.get(i)
                    .getMedoid().getTopic()));
        }
        return builder.toString();
    }

    /**
     * @return A Graphviz DOT represention of the clusters
     */
    public String toDot() {
        /*
         * Wir beginnen mit der Graph-Definition, die alle Cluster umschliessen
         * soll:
         */
        StringBuilder builder =
                new StringBuilder(String.format(
                        "graph clusters { label=\"Purity: %s\" node[shape=record] rankdir=TD\n",
                        getPurity()));
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            String label = cluster.getLabel();
            /* Dann schreiben wir für jeden Cluster einen Knoten raus... */
            builder.append(String.format("\t%s[label = \"{%s|%s", cluster.hashCode(), label,
                cluster.documents.size()));
            for (Document document : cluster) {
                /*
                 * ...der in einem Kästchen jedes Dokument im Cluster
                 * beschreibt:
                 */
                builder.append(String.format("|%s", document.getTopic()));
            }
            builder.append(String.format("}\"]\n"));
        }
        return builder.append("}\n").toString();
    }
}
