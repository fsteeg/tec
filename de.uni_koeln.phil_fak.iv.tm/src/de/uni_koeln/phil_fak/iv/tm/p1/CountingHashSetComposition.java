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
package de.uni_koeln.phil_fak.iv.tm.p1;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

/**
 * Demonstration of how favoring composition over inheritance maintains
 * encapsulation. This example is adopted from Effective Java, 2nd Ed., Item 16.
 * @author Fabian Steeg (fsteeg)
 * @param <E> The type of the set elements.
 */
public class CountingHashSetComposition<E> {
    private static final long serialVersionUID = 1L;
    private int attempts = 0;
    /*
     * Die Lösung: Komposition statt Vererbung. Um die Vorteile der Vererbung zu
     * haben (und unser Set ansprechen zu können wie andere Sets) würde man
     * zusätzlich Set implementieren (vgl. Document und Corpus). Um zudem nicht
     * immer alle Set-Methoden implementieren zu müssen kann man ein einziges
     * ForwardingSet implementieren, von dem wir dann erben. So haben wir alle
     * Vorteile ohne die Nachteile. Vgl. Effective Java, Second Edition, Item
     * 16; Design Patterns, Chapter 1).
     */
    private HashSet<E> set = new HashSet<E>();

    public int getAttempts() {
        return attempts;
    }

    public boolean add(E e) {
        attempts++;
        return set.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        attempts += c.size();
        return set.addAll(c);
    }

    private int size() {
        return set.size();
    }

    @Test
    public void test() {
        CountingHashSetComposition<String> set = new CountingHashSetComposition<String>();
        set.addAll(Arrays.asList("a", "b", "b"));
        Assert.assertEquals(2, set.size());
        /* Hier funktioniert es wie erwartet: */
        Assert.assertEquals(3, set.getAttempts());
    }

}
