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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus;
import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/*
 * Merkmalsberechung basierend auf TF (Termfrequenz, d.h. Termhäufigkeit im
 * Dokument) und IDF (invertierte Dokumentenfrequenz, d.h. Anzahl von
 * Dokumenten, in denen ein Term vorkommt).
 */
/**
 * TF/IDF feature computation.
 * @author Fabian Steeg (fsteeg)
 */
public final class TfIdfFeatures {

    private Document document;
    private Corpus corpus;

    /**
     * @param document The document to create the features for
     * @param corpus The corpus the given document is a part of
     */
    public TfIdfFeatures(final Document document, final Corpus corpus) {
        if (corpus.getTerms().size() == 0) {
            throw new IllegalArgumentException("Empty Corpus!");
        }
        this.document = document;
        this.corpus = corpus;
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.features.Features#vector()
     */
    public FeatureVector vector() {
        // Ein Vektor für dieses Dokument ist...
        List<Float> values = new ArrayList<Float>();
        // ...für jeden Term im Vokabular...
        Set<String> terms = corpus.getTerms();
        boolean ok = false;
        for (String dictionaryTerm : terms) {
            // der tf-idf-Wert des Terms:
            Float tfIdf = tfIdf(dictionaryTerm);
            if (tfIdf > 0) {
                ok = true;
            }
            values.add(tfIdf);
        }
        if (!ok) {
            // FIXME is this OK?
            // String warning =
            // "Warning: Created a TF-IDF vector without any activation for terms size: "
            // + terms.size();
            // System.out.println(warning);
            // throw new IllegalStateException(warning);
        }
        return new FeatureVector(values);
    }

    private Float tfIdf(final String dictionaryTerm) {
        /* TF und DF */
        Integer tf = document.getTermFrequencyOf(dictionaryTerm);
        tf = tf == null ? 0 : tf;
        Integer df = corpus.getDocumentFrequencyOf(dictionaryTerm);
        df = df == null ? 0 : df;
        /* IDF */
        float idf = (float) Math
                .log(corpus.getNumberOfDocuments() / (float) df);
        /* TF-IDF */
        float f = tf * idf;
        // if(f!=0f) System.err.println("TF-IDF: " + f);
        return f;
    }
}
