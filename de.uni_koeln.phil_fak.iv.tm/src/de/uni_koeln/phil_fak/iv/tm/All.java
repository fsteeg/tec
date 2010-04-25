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
package de.uni_koeln.phil_fak.iv.tm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.uni_koeln.phil_fak.iv.tm.p1.Praxis1;
import de.uni_koeln.phil_fak.iv.tm.p2.Praxis2;
import de.uni_koeln.phil_fak.iv.tm.p3.Praxis3;
import de.uni_koeln.phil_fak.iv.tm.p4.Praxis4;
import de.uni_koeln.phil_fak.iv.tm.p5.Praxis5;

/**
 * Run all tests. Requires data (created by running this as a Java
 * applications).
 * @author Fabian Steeg (fsteeg)
 */
@RunWith( Suite.class )
@Suite.SuiteClasses( { Praxis1.class, Praxis2.class, Praxis3.class, Praxis4.class, Praxis5.class } )
public class All {
    public static void main(String[] args) {
        System.out.println("Material for the course 'Text-Mining', University of Cologne.");
        String[] strings = {};
        Praxis1.main(strings);
        Praxis2.main(strings);
        Praxis3.main(strings);
        Praxis4.main(strings);
        Praxis5.main(strings);
    }

}
