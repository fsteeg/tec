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
package de.uni_koeln.phil_fak.iv.tm.p1;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_koeln.phil_fak.iv.ir.p6.crawler.Crawler;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.CorpusDatabase;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/*
 * Text-Mining (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html),
 * erste Praxissitzung (tm.p1). Fachlich: Korpuserstellung und Datenzugriff.
 * Technisch: Grundlagen des objektorientierten Designs, Objektdatenbank (DB4O)
 * und Native Queries. Basiert auf: Information-Retrieval
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-informationretrieval.html),
 * sechste Praxissitzung (ir.p6, Crawling, ist enthalten) und dem NekoHTML
 * parser (http://nekohtml.sourceforge.net/).
 */
/**
 * Material for the course 'Text-Mining', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html)
 * @author Fabian Steeg (fsteeg)
 */
public class Praxis1 {
    private Corpus corpus;

    public static void main(final String[] args) {
        /* Hier erstellen und crawlen (dauert). */
        Corpus c = CorpusDatabase.create("data/corpus-tm-1.db");
        List<Document> list = Crawler.crawl(1, "http://www.spiegel.de",
                "http://www.bild.de");
        c.addAll(list);
    }

    @Before
    public void before() {
        /* Hier (vor jedem Test) nur öffnen. */
        corpus = CorpusDatabase.open("data/corpus-tm-1.db");
    }

    @Test
    public void test() {
        List<Document> all = corpus.getDocuments();
        List<Document> spiegel = corpus.getDocumentsForSource("spiegel.de");
        List<Document> politik = corpus.getDocumentsForTopic("politik");
        /* Jetzt können wir erstmal überprüfen ob wir überhaupt was haben: */
        Assert.assertTrue("Found no documents at all", all.size() > 0);
        Assert.assertTrue("No documents source query", spiegel.size() > 0);
        Assert.assertTrue("No documents for topic query", politik.size() > 0);
        /*
         * Und unsere Erwartungen an die Ergebnisse festhalten (durch die
         * Strings, die als Fehlermeldung ausgegeben würden wenn die Überprüfung
         * fehlschlägt) und überprüfen:
         */
        Assert.assertTrue("Source result has wrong source", spiegel.get(0)
                .getSource().contains("spiegel.de"));
        Assert.assertEquals("politik", politik.get(0).getTopic());
        /* Kurzausgabe zur Info: */
        System.out.println("----------------------");
        System.out.println("Results for 'politik':");
        System.out.println("----------------------");
        for (Document document : politik) {
            System.out.println(document);
        }
    }

    @After
    public void after() {
        corpus.close();
    }
}
