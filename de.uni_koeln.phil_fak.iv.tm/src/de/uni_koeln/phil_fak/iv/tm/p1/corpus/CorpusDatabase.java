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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

import de.uni_koeln.phil_fak.iv.ir.p2.index.DocumentIndex;

/**
 * Persistent corpus implementation based on an object database (DB4O), using
 * native queries for document retrieval.
 * @author Fabian Steeg (fsteeg)
 */
public final class CorpusDatabase implements Corpus {
    /*
     * Diese Implementierung basiert auf DB4O. Bräuchte man eine der im Seminar
     * angesprochenen Alternativen (z.B. eine relationale DB oder XML), könnte
     * man eine weitere Implementierung des Corpus-Interface erstellen, die das
     * Ganze auf ihre Weise regelt. Die Klassen, die das Korpus über das
     * Interface benutzen müssten nicht angepasst werden.
     */
    private ObjectContainer db;
    private Corpus index;

    /**
     * @param location The location of the DB4O db.
     * @return The corpus db instance for the given location
     */
    public static Corpus open(final String location) {
        return new CorpusDatabase(location);
    }

    /**
     * @param location The location of the DB4O db.
     * @return A new, empty instance for the given location
     */
    public static Corpus create(final String location) {
        File f = new File(location);
        boolean ok = f.delete();
        if (!ok && f.exists()) {
            throw new IllegalArgumentException("Could not delete: " + f);
        }
        return new CorpusDatabase(location);
    }

    private CorpusDatabase(final String location) {
        db = Db4o.openFile(location);
        index = new DocumentIndex();
        List<Document> documents = this.getDocuments();
        for (Document document : documents) {
            index.add(document);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#addAll(java.util.List)
     */
    public void addAll(final List<? extends Document> list) {
        for (Document d : list) {
            add(d);
        }
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#add(de.uni_koeln.phil_fak.iv.tm.p1.corpus.Document)
     */
    public void add(final Document document) {
        db.store(document);
        index.add(document);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#getDocuments()
     */
    public List<Document> getDocuments() {
        return db.query(Document.class);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#getDocuments(java.lang.Class)
     */
    public <T extends Document> List<T> getDocuments(final Class<T> type) {
        return db.query(type);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#getDocumentsForSource(java.lang.String)
     */
    public List<Document> getDocumentsForSource(final String source) {
        @SuppressWarnings("serial") List<Document> set = db
                .query(new Predicate<Document>() {
                    public boolean match(final Document candidate) {
                        return candidate.getSource().contains(source);
                    }
                });
        return new ArrayList<Document>(set);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#getDocumentsForTopic(java.lang.String)
     */
    public List<Document> getDocumentsForTopic(final String topic) {
        @SuppressWarnings("serial") List<Document> set = db
                .query(new Predicate<Document>() {
                    public boolean match(final Document candidate) {
                        return candidate.getTopic().equals(topic);
                    }
                });
        return new ArrayList<Document>(set);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#close()
     */
    public void close() {
        db.close();
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#getTerms()
     */
    public Set<String> getTerms() {
        return index.getTerms();
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#getDocumentFrequencyOf(java.lang.String)
     */
    public Integer getDocumentFrequencyOf(final String dictionaryTerm) {
        return index.getDocumentFrequencyOf(dictionaryTerm);
    }

    /**
     * {@inheritDoc}
     * @see de.uni_koeln.phil_fak.iv.tm.p1.corpus.Corpus#getNumberOfDocuments()
     */
    public Integer getNumberOfDocuments() {
        return index.getNumberOfDocuments();
    }
}
