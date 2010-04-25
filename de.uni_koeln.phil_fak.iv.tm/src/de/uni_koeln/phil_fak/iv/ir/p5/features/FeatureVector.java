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
package de.uni_koeln.phil_fak.iv.ir.p5.features;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;

/*
 * Klasse zur Repräsentation eines Dokumentenvektors. Wird mit den eigentlichen
 * Werten instanziiert und ist mit unterschiedlichen Methoden der
 * Merkmalsberechnung verwendbar (bei uns sind die Werte die TF-IDF-Werte).
 */
/**
 * Representation of a feature vector.
 * @author Fabian Steeg (fsteeg)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class FeatureVector {
    @XmlList
    private List<Float> features;

    @SuppressWarnings("unused")
    // For JAXB
    private FeatureVector() {}

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s with %s values", getClass().getSimpleName(),
                features.size());
    }

    /**
     * @param values The values of this vector
     */
    public FeatureVector(final List<Float> values) {
        this.features = values;
    }

    /** @return The actual vector values */
    public List<Float> getValues() {
        return features;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FeatureVector)) {
            return false;
        }
        FeatureVector that = (FeatureVector) obj;
        return this.features.equals(that.features);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return features.hashCode();
    }
    
    /*
     * Der andere Vector, mit dem dieser Vektor verglichen werden soll; Gibt die
     * Kosinus-Ähnlichkeit von diesem Vektor und dem anderen Vektor zurück (hier
     * man könnte andere Maße verwenden und vergleichend evaluieren)
     */
    /**
     * @param other The vector to compare this vector to
     * @return The similarity of this vector and the other vector (a value
     *         between 0 and 1)
     */
    public Float similarity(FeatureVector other) {
        /*
         * Bevor wir mit den Berechnung beginnen, prüfen wir ob das überhaupt
         * funktionieren kann (sowas erleichtert die Fehlersuche): die zu
         * vergleichenden Vektoren müssen gleich lang sein, sonst stimmt
         * irgendwas überhaupt nicht:
         */
        if (this.features.size() != other.features.size()) {
            throw new UncomparableVectorsException(this, other);
        }
        float dotProduct = dot(other);
        float euclidianLengthProduct = euc(other);
        /*
         * Da die Winkel zwischen Vektoren in einem rein positiven
         * Koordinatensystem maximal 90 Grad betragen, ist die
         * Kosinusähnlichkeit immer ein Wert zwischen 0 und 1 und so ein
         * brauchbares Maß zur Bestimmung der Ähnlichkeit (wobei 1 "identisch"
         * und 0 "keine Ähnlichkeit" bedeutet)
         */
        float dist = dotProduct == 0 || euclidianLengthProduct == 0
                ? 0 : dotProduct / euclidianLengthProduct;
        /*
         * Obiges behaupten und vermuten wir, aber sowas hier und da zu
         * überprüfen macht die Fehlersuche einfacher und erhöht das Vertrauen
         * in die Korrektheit des Codes (wie auch oben für die
         * Eingangsbedingung):
         */
        if (dist < -0.0001f || dist > 1.0001f) {
            String message = "Cosine similarity must be between 0 and 1, but is: "
                    + dist;
            throw new IllegalStateException(message);
        }
        if (new Float(dist).isNaN()) {
            throw new IllegalStateException(
                    String
                            .format(
                                    "Distance computed by devision of dot product %s and euclidian distance %s is not a number",
                                    dotProduct, euclidianLengthProduct));
        }
        return dist;
    }

    private float euc(FeatureVector query) {
        /*
         * Hier lag unser Problem vom Ende der letzten Stunde: wir haben hier
         * ints verwendet, aber 0 als int plus 0.04 (z.B.) es null bliev null,
         * die erste summe mal null gab null, und irgendein dot-Produkt durch
         * null gab Unendlichkeit (oder wieder null, wenn wir das abfangen).
         */
        float sum1 = 0;
        float sum2 = 0;
        /*
         * Euklidische Länge: Wurzel aus der Summe der quadrierten Elemente
         * eines der Vektoren:
         */
        for (Float f : features) {
            sum1 += Math.pow(f, 2);
        }
        for (Float f : query.features) {
            sum2 += Math.pow(f, 2);
        }
        /*
         * Wir wollen das Produkt der euklidischen Längen der zwei Vektoren
         * (|V(d1)| |V(d2)|)
         */
        return (float) (Math.sqrt(sum1) * Math.sqrt(sum2));
    }

    private float dot(FeatureVector query) {
        /*
         * Das dot product ist die Summe der Produkte der korrespondierenden
         * Vektor-Werte:
         */
        float sum = 0;
        for (int i = 0; i < features.size(); i++) {
            sum += (features.get(i) * query.features.get(i));
        }
        return sum;
    }
}
