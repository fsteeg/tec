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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.uni_koeln.phil_fak.iv.ir.p2.index.TermIndex;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/*
 * Der Parser übernimmt für uns die Umwandlung einer URL in ein WebDocument, das
 * Inhalt und ausgehende Links der Seite an der URL enthält.
 */
/**
 * Parses a URL into a document representation, using NekoHTML
 * (http://nekohtml.sourceforge.net/).
 * @author Fabian Steeg (fsteeg)
 */
final class Parser {

    private Parser() {
    // Enforce non-instantiability with a private constructor.
    }

    private static StringBuilder builder;
    private static Set<String> links;

    /**
     * @param url The URL of the page to parse into a document
     * @return A document instance for the given URL
     */
    public static WebDocument parse(final String url) {
        /*
         * Als Parser verwenden wir NekoHTML, einen fehlerkorrigierenden Parser
         * (http://nekohtml.sourceforge.net/) der auf Xerces aufbaut:
         */
        DOMParser parser = new DOMParser();
        try {
            /* Dem Neko-Parser können wir einfach die URL als String übergeben: */
            parser.parse(url);
            /* Wir instanziieren die zu fülleneden Werte: */
            builder = new StringBuilder();
            links = new HashSet<String>();
            /* Und beginnen die Verarbeitung mit dem ersten Element: */
            org.w3c.dom.Document document = parser.getDocument();
            Node root = document.getFirstChild();
            process(root);
            /* Dann erzeugen wir aus den Werten unser Dokument-Objekt: */
            Document indexedDocument = new TermIndex(builder.toString());
            WebDocument doc = new WebDocument(url, links, indexedDocument);
            return doc;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void process(final Node node) {
        /*
         * Wir ermitteln die Elemente über ihre Namen. Man könnte auch mit
         * instanceof testen ob es etwa ein HTMLParagraphElement ist, allerdings
         * ist das weniger zuverlässig, da etwa XHTML-Dokumente nicht aus
         * solchen (HTML...), sondern aus anderen Elementen (in einem anderen
         * Namensraum als HTML-Elemente...) bestehen.
         */
        String name = node.getNodeName().toLowerCase();
        /* Inhalt ist für uns hier nur das, was in p-Tags steht: */
        if (name.equals("p")) {
            builder.append(node.getTextContent()).append("\n\n");
        } else if (name.equals("a")) {
            /* Und die Links merken wir uns auch: */
            if (node.hasAttributes()) {
                /*
                 * Wenn das a-Element ein "href"-Attribut hat, kommt dieses in
                 * die Liste der Links:
                 */
                Node item = node.getAttributes().getNamedItem("href");
                if (item != null) {
                    links.add(item.getNodeValue());
                }
            }
        }
        /*
         * Jetzt ist der aktuelle Knoten abgearbeitet, jetzt kommen kommt der
         * rekursive Aufruf für den nächsten Knoten auf der gleichen
         * Schatelungsebene (wenn es einen gibt):
         */
        Node sibling = node.getNextSibling();
        if (sibling != null) {
            process(sibling);
        }
        /*
         * Nachdem jeder je seinen Nachbarn aufgerufen hat, kommt jetzt das
         * ganze mit den untergeordneten:
         */
        Node child = node.getFirstChild();
        if (child != null) {
            process(child);
        }
    }
}
