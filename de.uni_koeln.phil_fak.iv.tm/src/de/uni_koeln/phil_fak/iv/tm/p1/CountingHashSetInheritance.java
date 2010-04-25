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
 * Demonstration of leaking encapsulation when using implementation inheritance.
 * This example is adopted from Effective Java, 2nd Ed., Item 16.
 * @author Fabian Steeg (fsteeg)
 * @param <E> The type of set elements
 */
public class CountingHashSetInheritance<E> extends HashSet<E> {
    private static final long serialVersionUID = 1L;
    private int attempts = 0;

    public int getAttempts() {
        return attempts;
    }

    /**
     * {@inheritDoc}
     * @see java.util.HashSet#add(java.lang.Object)
     */
    @Override
    public boolean add(E e) {
        attempts++;
        return super.add(e);
    }

    /**
     * {@inheritDoc}
     * @see java.util.AbstractCollection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        attempts += c.size();
        return super.addAll(c);
    }

    @Test
    public void test() {
        CountingHashSetInheritance<String> set = new CountingHashSetInheritance<String>();
        set.addAll(Arrays.asList("a", "b", "b"));
        Assert.assertEquals(2, set.size());
        /* This implementation is not OK: */
        Assert.assertNotSame(3, set.getAttempts());
    }
}
