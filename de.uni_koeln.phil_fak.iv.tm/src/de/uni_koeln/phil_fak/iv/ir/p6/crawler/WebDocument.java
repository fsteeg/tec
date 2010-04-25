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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import de.uni_koeln.phil_fak.iv.ir.p5.features.FeatureVector;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/*
 * Das Ergebnis der Verarbeitung einer URL: Ein Web-Dokument mit der
 * Herkunfts-URL (z.B. für Relevanz-Gewichtungen), dem eigentlichen Inhalt als
 * String und den ausgehenden Links (für weitergehendes Crawling etc.). Hier
 * haben wir die Links einfach im Dokument anstelle einer eigenen Frontier mit
 * front queues und back queues (siehe IR-Buch, Kap. 20)
 */
/**
 * Representation of a web site.
 * @author Fabian Steeg (fsteeg)
 */
public final class WebDocument implements Document {
    /* Hier haben wir nur jene Sachen, die speziell ein web document ausmachen: */
    private String url;
    private Set<String> links;
    private long checksum;
    /*
     * Andere Aufgaben werden an ein aggregiertes Document delegiert (anstatt
     * die Implementierung zu erben, wir erben nur das Interface):
     */
    private Document index;

    /**
     * @param url The URL this document represents
     * @param links The outgoing links of the document
     * @param document The document to delegate to
     */
    public WebDocument(final String url, final Set<String> links,
            final Document document) {
        if (document == null) {
            throw new IllegalStateException("Null Document!");
        }
        /* Forward-Instanz: */
        this.index = document;
        /* Die Besonderheiten von dieser Art von Dokument: */
        this.url = url;
        /* Die Links müssen normalisiert werden: */
        Set<String> cleanLinks = LinkHelper.normalize(links, url);
        /* Und sollten keine verbotenen Ziele haben: */
        cleanLinks = LinkHelper.allowed(cleanLinks, url);
        this.links = cleanLinks;
        /*
         * Für den effizienten Vergleich bilden wir eine Checksum für den Inhalt
         * des Dokument, der zum Vergleich benutzt wird (siehe equals() weiter
         * unten). Jave bietet verschiedene Implementierung (Strg-T auf Checksum
         * zeigt die, wie bei allen Interfaces)
         */
        Checksum cs = new CRC32();
        cs.update(document.getContent().getBytes(), 0, document.getContent()
                .length());
        this.checksum = cs.getValue();

    }

    /**
     * @param document The document to copy
     */
    public WebDocument(final WebDocument document) {
        this(document.url, document.links, document.index);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof WebDocument)) {
            return false;
        }
        /* Vergleich der Checksums statt der Inhalte selbst (effizienter): */
        return checksum == ((WebDocument) obj).checksum;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        /*
         * Wenn man equals ueberschreibt, muss man auch hashCode ueberschreiben,
         * da die Collection-Klassen beides pruefen
         */
        return (checksum + "").hashCode();
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            return checked(new URL(this.url).toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return checked("[malformed location]");
        }
    }

    private String checked(final String url) {
        return String.format("%s %s", this.getClass().getSimpleName(),
                this.checksum);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.storage.Document#getSource()
     */
    public String getSource() {
        return this.url;
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.storage.Document#getTopic()
     */
    public String getTopic() {
        Extractor.Location[] values = Extractor.Location.values();
        for (Extractor.Location location : values) {
            if (this.getSource().matches(location.getPattern())) {
                return new Extractor(location).extract(this.getSource());
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.storage.Document#getContent()
     */
    public String getContent() {
        return index.getContent();
    }

    /**
     * @return The outgoing links of this web document.
     */
    public Set<String> getLinks() {
        return links;
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document#getTerms()
     */
    public Set<String> getTerms() {
        return index.getTerms();
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document#getVector(de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus)
     */
    public FeatureVector getVector(final Corpus corpus) {
        return index.getVector(corpus);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document#getTermFrequencyOf(java.lang.String)
     */
    public Integer getTermFrequencyOf(final String dictionaryTerm) {
        return index.getTermFrequencyOf(dictionaryTerm);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document#getLocation()
     */
    public URL getLocation() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
