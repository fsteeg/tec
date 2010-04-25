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
package de.uni_koeln.phil_fak.iv.tm.p2.annotation;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * Generic Standoff Annotation with XML Binding.
 * @param <T> The type of the annotation
 * @author Fabian Steeg (fsteeg)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Annotation<T> {

    private T value;
    @XmlAttribute
    private URL location;
    @XmlAttribute
    private Integer end;
    @XmlAttribute
    private Integer start;

    /*
     * Wir verwenden eine Factory-Methode um Typinferenz zu bekommen, die uns
     * die redundante Typdeklaration erspart, wie z.B. @code{Set<String> s = new
     * HashSet<String>()}, stattdessen hier z.B. @code{Annotation<String> =
     * Annotation.of(...)}.
     */
    /**
     * @param <T> The type of the annotation value
     * @param data The data location
     * @param value The annotation value
     * @param start The start offset
     * @param end The end offset
     * @return An annotation of type T
     */
    public static <T> Annotation<T> of(final URL data, final T value,
            final int start, final int end) {
        return new Annotation<T>(data, value, start, end);
    }

    // For JAXB
    private Annotation() {}

    private Annotation(final URL location, final T vector, final Integer start,
            final Integer end) {
        this.location = location;
        this.value = vector;
        this.start = start;
        this.end = end;
    }

    /**
     * @return The offset this annotation ends in
     */
    public int getEnd() {
        return end;
    }

    /**
     * @return The offset this annotation starts at
     */
    public int getStart() {
        return start;
    }

    /**
     * @return The value of this annotation
     */
    public T getValue() {
        return value;
    }

    /**
     * @return The location of the annotated data
     */
    public URL getLocation() {
        return location;
    }

    /**
     * @return An XML representation of this Annotation, can be passed into
     *         {@link #fromXml(String, Class)}
     */
    public String toXml() {
        try {
            Marshaller marshaller = JAXBContext.newInstance(Annotation.class,
                    value.getClass()).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param <T> The type of the annotation
     * @param xml The XML to deserialize
     * @param type The type of the annotation to deserialize
     * @return An annotation of type T, deserialized from the XML
     */
    public static <T> Annotation<T> fromXml(final String xml,
            final Class<T> type) {
        Unmarshaller unm;
        try {
            unm = JAXBContext.newInstance(Annotation.class, type)
                    .createUnmarshaller();
            Annotation<?> annotation = (Annotation<?>) unm
                    .unmarshal(new StringReader(xml));
            /*
             * Hier kapseln wir die Brücke zwischen typsicher und unsicher: wir
             * machen den unsicheren cast hier, dafür sind alle die diese API
             * nutzen typsicher (und hantieren nicht mit Annotation<?>-Objekten)
             */
            @SuppressWarnings("unchecked") Annotation<T> result = (Annotation<T>) annotation;
            return result;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param location The location to store the generated XSD file
     */
    public static void generateXmlSchema(final String location) {
        try {
            JAXBContext context = JAXBContext.newInstance(Annotation.class);
            final File file = new File(location);
            /*
             * API hier etwas komplexer als oben wegen der anoanymen inneren
             * Klasse, aber im Grunde gibt man ihm einfach das File wo das
             * Schema hingeschrieben werden soll:
             */
            context.generateSchema(new SchemaOutputResolver() {
                @Override
                public Result createOutput(final String namespaceUri,
                        final String suggestedFileName) throws IOException {
                    return new StreamResult(file);
                }
            });
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Und hier das Minimalprogramm einer jeden vernünftigen Klasse: toString,
     * equals und hashCode aus Object überschreiben:
     */

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s of type %s from %s to %s as: %s (data at %s)",
                this.getClass().getSimpleName(), value.getClass()
                        .getSimpleName(), start, end, value, location);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Annotation<?>)) {
            return false;
        }
        /* This is safe because we checked the type above: */
        Annotation<?> that = (Annotation<?>) obj;
        /*
         * Using URL#toString for comparison because URL#equals does domain
         * lookup:
         */
        return this.location.toString().equals(that.location.toString())
                && this.start.equals(that.start) && this.end.equals(that.end)
                && this.value.equals(that.value);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        /*
         * Empfohlenes Verfahren für das Hashing (aus Eff. Java, s. Lit.):
         * ergibt stets recht gut verteilte Werte (optimales Hashing ist eine
         * Wissenschaft für sich)
         */
        int oddPrime = 31;
        int result = 1;
        /*
         * Using URL#toString for hashing because URL#hashCode does domain
         * lookup:
         */
        result = oddPrime * result + location.toString().hashCode();
        result = oddPrime * result + value.hashCode();
        result = oddPrime * result + start.hashCode();
        result = oddPrime * result + end.hashCode();
        return result;
    }
}
