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
package de.uni_koeln.phil_fak.iv.tm.p1.corpus;

import java.net.URL;
import java.util.Set;

import de.uni_koeln.phil_fak.iv.ir.p5.features.FeatureVector;

/**
 * Interface to a document.
 * @author Fabian Steeg (fsteeg)
 */
public interface Document {
    /** @return The source or origin of this document. */
    String getSource();

    /** @return The topic of this document. */
    String getTopic();

    /** @return The actual document content. */
    String getContent();

    /**
     * @return The terms in this document
     */
    Set<String> getTerms();

    /**
     * @param corpus The context corpus to be used when comuting a vector for
     *            this document
     * @return A feature vector representation of this document as part of the
     *         given corpus
     */
    FeatureVector getVector(Corpus corpus);

    /**
     * @param dictionaryTerm The dictionary term
     * @return The frequency opf the given term in this document
     */
    Integer getTermFrequencyOf(String dictionaryTerm);

    /**
     * @return The location of this document
     */
    URL getLocation();
}
