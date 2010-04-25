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
package de.uni_koeln.phil_fak.iv.ir.p2.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Präprozessor, der aus Strings Terme macht.
 */
/**
 * Simple preprocessor.
 * @author Fabian Steeg (fsteeg)
 */
public final class Preprocessor {
    /*
     * Ein Unicode-wirksamer Ausdruck für "Nicht-Buchstabe", der auch Umlaute
     * berücksichtigt; die einfache Version ist: "\\W"
     */
    private static final String UNICODE_AWARE_DELIMITER = "[^\\p{L}]";

    private String delimiter;
    private List<String> specialCases;

    /*
     * Verfügbare Musterbeschreibungen für zu extrahierenden Formate. Der
     * Vorteil der Verwendung einer Enum (anstelle von Konstanten) liegt darin,
     * dass so alle in dieser Enum enthaltenen Muster im Default-Konstruktor des
     * Preprocessor ausgelesen werden können.
     */
    /**
     * Patterns for special entities in texts.
     * @author Fabian Steeg (fsteeg)
     */
    public enum ExtractionPattern {
        /*
         * Regulärer Ausdruck für einfache Telefonnummern (0221-4701751),
         * Versionsnummern (8.04), Geldbeträge (3,50) und Uhrzeiten (15:15).
         */
        COMPOUND_NUMBER("\\d+[-.,:]\\d+"),
        /*
         * Emailadressen für einige Domains, mit Unterstützung von Punkten im
         * Domainnamen (wie in fsteeg@spinfo.uni-koeln.de).
         */
        EMAIL("[^@\\s]+@.+?\\.(de|com|eu|org|net)");

        /*
         * Der reguläre Ausdruck, der dieses Muster beschreibt.
         */
        /**
         * @return The regular expression describing the entity.
         */
        public String getVal() {
            return val;
        }

        private String val;

        /**
         * @param regularExpression The regulsr expression describing this
         *            extraction pattern.
         */
        ExtractionPattern(final String regularExpression) {
            this.val = regularExpression;
        }
    }

    /*
     * Reguläre Ausdrücke für die zu extrahierenden Muster, Regulärer Ausdruck
     * für das Token-Trennelement
     */
    /**
     * @param specialCases The list regular expressions describing special cases
     * @param tokenDelimiter The token delimiter pattern to be used for
     *            tokenization
     */
    public Preprocessor(final List<String> specialCases,
            final String tokenDelimiter) {
        this.specialCases = specialCases;
        this.delimiter = tokenDelimiter;
    }

    /*
     * Ein Standard-Präprozessor, der alle verfügbaren Muster extrahiert.
     */
    /**
     * A preprocessor with default special cases and default token delimiter
     * pattern.
     */
    public Preprocessor() {
        specialCases = new ArrayList<String>();
        for (ExtractionPattern p : ExtractionPattern.values()) {
            specialCases.add(p.getVal());
        }
        delimiter = UNICODE_AWARE_DELIMITER;
    }

    /**
     * @param text The text to tokenize
     * @return Returns the tokens the text was split into
     */
    public List<String> process(final String text) {
        String resultText = text;
        List<String> doc = new ArrayList<String>();
        for (String s : specialCases) {
            Pattern p = Pattern.compile(s);
            Matcher m = p.matcher(resultText);
            while (m.find()) {
                String group = m.group();
                doc.add(group);
                /*
                 * Hier muss man aufpassen: replaceAll nimmt einen regulären
                 * Ausdruck als erstes Argument. Das ist nicht was wir wollen,
                 * weil die extrahierten Muster als reguläre Ausdrücke wieder
                 * etwas anderes bedeuten, oder erst gar keine gültigen
                 * regulären Ausdrücke sind. Daher nehmen wir hier die
                 * replace-Methode, die einen einfachen String sucht und
                 * ersetzt.
                 */
                resultText = resultText.replace(group, "");
            }
        }
        List<String> list = Arrays.asList(resultText.split(delimiter));
        for (String s : list) {
            if (!s.trim().equals("")) {
                doc.add(s.toLowerCase());
            }
        }
        return doc;
    }

}
