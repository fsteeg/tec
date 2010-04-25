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

/**
 * Exception for unexpected vector comparison.
 * @author Fabian Steeg (fsteeg)
 */
class UncomparableVectorsException extends RuntimeException {
    transient private FeatureVector v1;
    transient private FeatureVector v2;

    /***/
    private static final long serialVersionUID = 8357404165430077570L;

    /**
     * {@inheritDoc}
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return String.format("Can't compare %s and %s", v1, v2);
    }

    /**
     * @param v1 The first vector
     * @param v2 The second vector
     */
    public UncomparableVectorsException(FeatureVector v1, FeatureVector v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
}
