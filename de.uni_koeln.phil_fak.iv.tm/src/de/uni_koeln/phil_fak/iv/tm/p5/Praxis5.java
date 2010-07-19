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
package de.uni_koeln.phil_fak.iv.tm.p5;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_koeln.phil_fak.iv.ir.p6.crawler.Crawler;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.CorpusDatabase;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;
import de.uni_koeln.phil_fak.iv.tm.p5.clustering.Cluster;
import de.uni_koeln.phil_fak.iv.tm.p5.clustering.ClusterAnalysis;

/*
 * Text-Mining (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html),
 * fünfte Praxissitzung (tm.p5). Fachlich: flaches k-Means-Clustering und
 * Purity-Evaluierung für Spiegel-Online-Artikeln über TF-IDF-Vektoren und
 * Kosinusähnlichkeit. Technisch: Benutzung von Teilen der Java Concurrency API
 * (CopyOnWriteArrayList, ExecutorService), Visualisierung mit Graphviz DOT;
 * Verwendet: ir.5 (Kosinusähnlichkeit für Ranking im IR)
 */
/**
 * Material for the course 'Text-Mining', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html)
 * @author Fabian Steeg (fsteeg)
 */
public class Praxis5 {
    private static final String DATA = "output/corpus-tm-5.db";
    private Corpus corpus;
    private List<Document> documents;
    private ClusterAnalysis analysis;
    private static final int ITERATIONS = 5;
    private static final int CLUSTER_START = 3;
    private static final int CLUSTER_END = 12;

    public static void main(final String[] args) {
        /* Hier (= Run as -> Java application) erstellen und crawlen (dauert). */
        Corpus c = CorpusDatabase.create(DATA);
        List<Document> documents = Crawler.crawl(1, 40, "http://www.spiegel.de");
        c.addAll(documents);
    }

    @Before
    public void before() {
        /* Hier (vor jedem Test) nur öffnen. */
        corpus = CorpusDatabase.open(DATA);
        documents = corpus.getDocumentsForSource("spiegel.de");
        System.out.println(String.format("Clustering %s documents, %s iterations",
                documents.size(), ITERATIONS));
        /* Hier erzeigen wir unsere Cluster-Analyse */
        analysis = new ClusterAnalysis(corpus, documents);
        System.out.println("------------------------------------------------");
    }

    @Test
    public void clusterSingle() {
        /* Wir clustern für ein k: */
        List<Cluster> clusters = analysis.analyse(CLUSTER_END, ITERATIONS);
        Assert.assertNotNull(clusters);
    }

    @Test
    public void clusterMulti() {
        /*
         * Wir clustern für mehrer k zwischen CLUSTER_START und CLUSTER_END. Die
         * parallele Ausführung habe ich hier hinter die API verpackt, so dass
         * man sich als API-Client damit nicht beschäftigen muss.
         */
        List<List<Cluster>> clustersForEachK =
                analysis.analyse(CLUSTER_START, CLUSTER_END, ITERATIONS);
        Assert.assertNotNull(clustersForEachK);
    }

    @After
    public void after() {
        /* Hier (nach jedem Test) schliessen. */
        corpus.close();
    }

}
