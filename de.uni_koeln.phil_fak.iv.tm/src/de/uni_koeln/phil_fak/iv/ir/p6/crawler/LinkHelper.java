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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/*
 * Einfache Normalisierung von relativen Links und Filtern von in der robots.txt
 * ausgeschlossenen Links (mit einigem Optimierungspotential bei Effizienz und
 * Zuverlässigkeit)
 */
/**
 * Helper class for working with links.
 * @author Fabian Steeg (fsteeg)
 */
final class LinkHelper {
    private LinkHelper() {
    // Enforce non-instantiability with a private constructor
    }

    /*
     * Normalisiert die Werte in dem Set indem die URL vorne angeheangen wird
     * (wenn der Wert normalisiert werden muss). Hier gibt es viele
     * weitergehende Fallstricke und dies ist nur ein einfacher Anfang (z.B.
     * javascript, links auf die gleiche Seite etc.)
     */
    /**
     * Simple URL normalization.
     * @param urls The URLs no normalize
     * @param url The parent URL: the URL of the page the given URLs are found
     * @return The normalized URLs
     */
    public static Set<String> normalize(final Set<String> urls, final String url) {
        Set<String> normalized = new HashSet<String>();
        String fixedUrl = url + (url.endsWith("/") ? "" : "/");
        for (String s : urls) {
            if (!s.startsWith("javascript")) {
                if (!s.startsWith("http:")) {
                    if (s.startsWith("/")) {
                        s = s.substring(1);
                    }
                    normalized.add((fixedUrl + s));
                } else {
                    normalized.add(s);
                }
            }
        }
        return normalized;
    }

    /*
     * Filtert die durch die an der angegebenen URL eventuell vorhandenen
     * robots.txt ausgeschlossenen Links in der Liste aus
     */
    /**
     * @param links The links to check
     * @param url The URL the links are found on
     * @return Returns those of the given links that are allowed (i.e. not
     *         excluded on the URL via robots.txt)
     */
    public static Set<String> allowed(final Set<String> links, final String url) {
        /* Verbotene Verzeichnisse aus der robots.txt holen: */
        Set<String> disallowed = checkRobots(url);
        /* Die sind relativ und müssen auch normalisiert werden: */
        disallowed = LinkHelper.normalize(disallowed, url);
        /* Eine frisches Set von Links... */
        Set<String> allowed = new HashSet<String>();
        for (String link : links) {
            /* ...in das nur jene kommen, die erlaubt sind... */
            if (!disallowed(link, disallowed)) {
                allowed.add(link);
            }
        }
        /* ...ist unser Ergebnis: */
        return allowed;
    }

    private static boolean disallowed(final String url,
            final Set<String> disallowed) {
        for (String d : disallowed) {
            /*
             * Wenn eine URL mit einem Eintrag in der robots.txt beginnt, soll
             * diese nicht gecrawled werden:
             */
            if (url.startsWith(d)) {
                System.out.println("[robots] " + url
                        + " is not allowed and will not be crawled");
                return true;
            }
        }
        return false;
    }

    /*
     * Ein einfacher Versuch die robots.txt auszulesen (nicht sehr zuverlässig
     * und wenig effiziet, da wir jedesmal neu holen, auch wenn es ständig die
     * selbe ist - könnte man in einer Map ablegen)
     */
    private static Set<String> checkRobots(final String url) {
        Set<String> disallowed = new HashSet<String>();
        String fixedUrl = url.trim();
        try {
            URL u = new URL(fixedUrl);
            /*
             * Herr Molina hatte im Gegensatz zu dem, was ich in der Übung sagte
             * recht: nach der robots.txt schauen wir nur im Root des hosts:
             */
            String host = u.getHost();
            /*
             * Wir bereiten die URL auf das Anhängen der robots.txt vor:
             */
            if (!host.endsWith("/")) {
                host += "/";
            }
            URL rUrl = new URL("http://" + host + "robots.txt");
            Scanner s = new Scanner(rUrl.openStream());
            /* Wenn wir so weit kommen, lesen wir zeilenweise: */
            boolean read = false;
            StringBuilder builder = new StringBuilder();
            while (s.hasNextLine()) {
                String nextLine = s.nextLine();
                builder.append(nextLine).append("\n");
                /* Wir beginnen uns alles zu merken wenn wir sowas lesen: */
                if (nextLine.contains("*") && nextLine.contains("User-agent")) {
                    read = true;
                } else if (read) {
                    if (!nextLine.contains("User-agent")
                            && nextLine.contains(":")) {
                        String[] strings = nextLine.split(":");
                        if (strings.length > 1) {
                            disallowed.add(strings[1].trim());
                        }
                    } else {
                        /*
                         * Und wenn wir am lesen waren und wieder was von
                         * user-agent kommt, hören wir auf:
                         */
                        read = false;
                    }
                }
            }
        } catch (MalformedURLException e1) {
            System.err.println("Malformed URL: " + e1.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return disallowed;
    }
}
