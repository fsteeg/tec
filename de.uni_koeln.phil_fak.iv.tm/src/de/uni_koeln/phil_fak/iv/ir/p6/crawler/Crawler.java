/**
 * Material for the course 'Information-Retrieval', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-informationretrieval.html)
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
package de.uni_koeln.phil_fak.iv.ir.p6.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/*
 * Ein einfacher paralleler Crawler: beginnend mit einer Menge von URLs werden
 * diese zu Web-Dokumenten gecrawled und Links bis zum angegebenen Level
 * gefolgt. Die Ergebnisse werden in die übergebene List gepackt.
 */
/**
 * A simple concurrent crawler.
 * @author Fabian Steeg (fsteeg)
 */
public final class Crawler {

    private Crawler() {
    // Enforce non-instantiability with a private constructor
    }

    /**
     * @param seed The pages to use as the starting point for the crawl
     * @param depth The depth of the crawl (0 means only the seed pages, 1 means
     *            follow all links on the seed pages, etc.)
     * @return The document representations of the crawled web pages
     */
    public static List<Document> crawl(final int depth, final String... seed) {
        return crawl(depth, Integer.MAX_VALUE, seed);
    }

    /**
     * @param seed The pages to use as the starting point for the crawl
     * @param depth The depth of the crawl (0 means only the seed pages, 1 means
     *            follow all links on the seed pages, etc.)
     * @param max the maximum number of documents to crawl
     * @return The document representations of the crawled web pages
     */
    public static List<Document> crawl(final int depth, final int max,
            final String... seed) {
        /*
         * Früher benutzte man zur parallelen Programmierung unter Java Threads
         * direkt, sowohl als Einheit der Arbeit, als auch um diese auszuführen.
         * Diese beiden Dinge trennt man inzwischen: Einheit für die Arbeit ist
         * ein Runnable, zum Ausführen nimmt man einen executor service (vgl.
         * Effective Java, Second Edition, Kap. 10):
         */
        ExecutorService exec = Executors.newCachedThreadPool();
        /* Das Ergebnis des Crawling wird eine Menge von Web-Dokumenten sein: */
        List<Document> docs = new ArrayList<Document>();
        /*
         * Um hier etwa nicht parallel zu arbeiten würde man sich mit
         * "Executors.newSingleThreadExecutor()" einen anderen service holen,
         * oder mit "Executors.newFixedThreadPool(2)" eine, der nicht immer mehr
         * Threads erzeugt sondern immer mit 2 Threads arbeitet.
         */
        for (String url : seed) {
            /*
             * Für jede Start-URL starten wir ein Runnable, diese werden dann
             * parallel ausgeführt. Die ausgehenden Links werden dann nicht
             * wieder neu parallel ausgeführt, weil dies zu viele Anfragen an
             * einen einzgen host zu folge hätte (siehe Kommentar in
             * CrawlRunnable)
             */
            exec.execute(new CrawlerRunnable(url, depth, docs, max));
        }
        /* Dass der Executor irgendwann stoppen soll muss man ihm sagen: */
        exec.shutdown();
        /*
         * Jetzt sind wir am laufen... wenn wir jetzt nichts täten wären wir
         * raus und die Threads laufen weiter. Wir wollen aber am Ende was mit
         * de Ergebnissen machen (später indizieren und darauf suchen, jetzt
         * erstmal nur ansehen wie viele Dokumente wir haben), also warten wir
         * auf die Vollendung der Arbeit (und zwar ohne echten timeout):
         */
        boolean b;
        try {
            b = exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            /*
             * Hätten wir einen kürzeren Timeout angegeben, wäre der
             * resultierende boolean false, wenn aufgrund des Timeouts
             * abgebrochen worden wäre (siehe JavaDoc von awaitTermination):
             */
            System.out.println("Durchgelaufen: " + b);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return docs;
    }

}
