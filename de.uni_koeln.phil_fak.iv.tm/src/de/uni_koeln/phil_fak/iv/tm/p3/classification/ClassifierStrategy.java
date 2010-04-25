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
package de.uni_koeln.phil_fak.iv.tm.p3.classification;

import de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document;

/**
 * Strategy interface for classifier implementations.
 * @author Fabian Steeg (fsteeg)
 */
public interface ClassifierStrategy {

    /**
     * @param document The document to classify
     * @return The class label for the document
     */
    String classify(Document document);

    /**
     * @param document The document to train the classifier with
     * @param classLabel The correct class label for the document
     * @return The altered, trained classifier
     */
    /*
     * Anders als im Seminar ist die train-Methode nicht void, sondern
     * ermöglicht den Classifier zurückzugeben. Immer wenn man in einem
     * Interface ein void-Methode deklariert sollte man aufmerksam werden, denn
     * eine void-Methode zwingt alle Implementierungen des Interface veränderbar
     * (mutable) zu sein. Objekte, die einmal erzeugt nicht mehr verändert
     * werden können (immutable) haben aber viele Vorteile, etwa im Zusammenhang
     * mit paralleler Programmierung. Man sollte also möglichst nicht über das
     * Interface veränderbare Klassen erzwingen. Wenn man einfach eine Instanz
     * des Interface zurückgibt, können Implementierungen optional eine Kopie
     * von sich zurückgeben. Oder einfach sich selbst zurückgeben, was wir in
     * unserer Implementierung machen.
     */
    ClassifierStrategy train(Document document, String classLabel);
}
