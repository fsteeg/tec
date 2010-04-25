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
package de.uni_koeln.phil_fak.iv.ir.p6.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract topics from a URL.
 * @author Fabian Steeg (fsteeg)
 */
class Extractor {

    /**
     * Enum describing topic extraction strategies for different web sites.
     * @author Fabian Steeg (fsteeg)
     */
    public enum Location {
        /***/
        SPIEGEL("http://www.spiegel.de/([^/]+?)/.*"),
        /***/
        BILD("http://www.bild.de/BILD/([^/]+?)/.*");
        private String pattern;

        private Location(final String r) {
            this.pattern = r;
        }

        /**
         * @return The pattern describing URLs for the location
         */
        String getPattern() {
            return pattern;
        }
    }

    private Pattern pattern;

    /**
     * @param location The location this extractor shoudl work for
     */
    public Extractor(final Location location) {
        pattern = Pattern.compile(location.pattern);
    }

    /**
     * @param string The URL string to extracta topic from
     * @return The topic extracted from the URL string or null if there is no
     *         match
     */
    public String extract(final String string) {
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            /*
             * Der erste (index 0) match ist der ganze Bereich, der durch den
             * reg. Ausdruck beschrieben wird, die erste Gruppe (was wir
             * extrahieren wollen) ist als 1:
             */
            String group = m.group(1);
            return group;
        }
        return null;
    }
}
