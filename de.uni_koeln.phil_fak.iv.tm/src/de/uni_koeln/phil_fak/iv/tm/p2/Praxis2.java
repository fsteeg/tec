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
package de.uni_koeln.phil_fak.iv.tm.p2;

import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_koeln.phil_fak.iv.ir.p5.features.FeatureVector;
import de.uni_koeln.phil_fak.iv.ir.p6.crawler.Crawler;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.CorpusDatabase;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;
import de.uni_koeln.phil_fak.iv.tm.p2.annotation.Annotation;

/*
 * Text-Mining (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html),
 * zweite Praxissitzung (tm.p2). Fachlich: Merkmalsberechnung und
 * Datenaufbereitung. Technisch: Delegation statt Vererbung als Designprinzip,
 * Annotation als Austauschformat im TM, Generics für typsichere Annotationen,
 * XML-Binding und Schemagenerierung für Export und Import. Basiert auf:
 * Information-Retrieval
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-informationretrieval.html),
 * fünfte Praxissitzung (ir.p5, TF-IDF-Vektoren, ist enthalten) und zweite
 * Praxissitzung (ir.p2, Index).
 */
/**
 * Material for the course 'Text-Mining', University of Cologne.
 * (http://www.spinfo.phil-fak.uni-koeln.de/spinfo-textmining.html)
 * @author Fabian Steeg (fsteeg)
 */
public class Praxis2 {
    private static final String DATA = "output/corpus-tm-2.db";
    private Corpus corpus;
    private Document document;

    /*
     * Das Drumherum: Korpus einmal crawlen, öffnen und schliessen vor und nach
     * jedem Test (jeder Test soll atomar sein, alleine und unabhängig von den
     * anderen ausführbar).
     */

    public static void main(final String[] args) {
        /* Hier erstellen und crawlen (dauert). */
        Corpus c = CorpusDatabase.create(DATA);
        List<Document> documents = Crawler.crawl(1, "http://www.spiegel.de",
                "http://www.bild.de");
        c.addAll(documents);
    }

    @Before
    public void before() {
        /* Hier (vor jedem Test) nur öffnen. */
        corpus = CorpusDatabase.open(DATA);
        document = corpus.getDocumentsForSource("spiegel").get(0);
        System.out.println("-----------------------------------------------------------------------------------------------------");
    }

    @After
    public void after() {
        /* Hier (nach jedem Test) schliessen. */
        corpus.close();
    }

    /*
     * Die Inhalte der Sitzung: Merkmale, Annotation, Generics, XML-Binding.
     */

    @Test
    public void vector() {
        /*
         * Durch den Einbau der IR-Komponenten bekommen wir nun von den
         * Dokumenten, die in unserer Korpus-Datenbank sind Merkmalsvektoren:
         */
        FeatureVector vector = document.getVector(corpus);
        Assert.assertTrue("Vector is null", vector != null);
        Assert.assertTrue("Vector contains no elements", vector.getValues()
                .size() > 0);
        System.out.println("Vector values:\n" + vector.getValues());
    }

    @Test
    public void annotation() {
        /*
         * Mit unseren generischen Annotations können wir einfache Annotationen
         * erstellen, bei denen der Wert ein bereits vorhandener Typ ist wie
         * String:
         */
        URL location = document.getLocation();
        int end = document.getContent().length();
        String value = "Spiegel ONLINE";
        Annotation<String> simple = Annotation.of(location, value, 0, end);
        System.out.println("Simple annotation: " + simple);
        /*
         * Aber auch selbst definierte komplexe Typen wie unser FeatureVector,
         * so dass wir die jetzt verfügbaren Vektoren komplett in unser
         * TM-System integriert haben:
         */
        FeatureVector vector = document.getVector(corpus);
        Annotation<FeatureVector> custom = Annotation.of(location, vector, 0,
                end);
        System.out.println("Custom annotation: " + custom);
    }

    @Test
    public void marshalling() {
        /* Durch XML-Binding können wir unsere Annotationen exportieren: */
        FeatureVector vector = document.getVector(corpus);
        Annotation<FeatureVector> custom = Annotation.of(
                document.getLocation(), vector, 0, document.getContent()
                        .length());
        String xml = custom.toXml();
        System.out.println("Annotation as XML:\n" + xml);
        /* Und wieder importieren: */
        Annotation<FeatureVector> unmarshalled = Annotation.fromXml(xml,
                FeatureVector.class);
        System.out.println("Ummarshalled: " + unmarshalled);
        /*
         * Und wir erwarten dass die Original-Instanz mit der de-serialisierten
         * identisch ist:
         */
        Assert.assertEquals(custom, unmarshalled);
    }

    @Test
    public void generateXmlSchema() {
        /*
         * Um unsere exportierten Annotationen validierbar zu machen, und um
         * Annotationen die importiert werden zu validieren, können wir eine XSD
         * benutzen. Die XSD von Hand zu pflegen ist eine mühsame Verdoppelung
         * unserer Arbeit, denn unser Schema ist ja eigentlich die Java-Klasse.
         * Dort sagen wir ja wie Annotationen beschaffen sind. Deshalb nutzen
         * wir JAXB um aus der Klasse ein Scham zu generieren (auch eine Art von
         * MDD):
         */
        String location = "output/annotation.xsd";
        Annotation.generateXmlSchema(location);
        Assert.assertTrue("XSD was not created!", new File(location).exists());
        System.out.println("Generated schema: " + location);
    }
}
