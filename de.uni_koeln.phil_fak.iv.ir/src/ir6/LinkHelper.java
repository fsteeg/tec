package ir6;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.jrobotx.RobotExclusion;

/*
 * Einfache Normalisierung von relativen Links und Filtern von in der robots.txt ausgeschlossenen
 * Links (mit einigem Optimierungspotential bei Effizienz und Zuverlässigkeit)
 */
/**
 * Helper class for link normalization and robots.txt checking.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class LinkHelper {
  private LinkHelper() {} // enforce non-instantiability

  /**
   * @param url The base URL
   * @param links The links given at the base URL that should be checked
   * @return The normalized, allowed links
   * @throws MalformedURLException If the given links contained an invalid URL
   */
  static Set<String> checked(final String url, final Set<String> links)
      throws MalformedURLException {
    /* Die high-level Logik: gibt die normalisierten, erlaubten Links zurück: */
    return allowed(normalized(url, links));
  }

  private static Set<String> normalized(final String base, final Set<String> links)
      throws MalformedURLException {
    /* Für eine benutzbare root-URL (z.B. http://nlp.stanford.edu/IR-book/html/htmledition/)... */
    String url = removeFile(base);
    Set<String> result = new HashSet<String>();
    /* Normalisieren wir jeden Link: */
    for (String link : links) {
      result.add(normalized(url, link));
    }
    return result;
  }

  private static String removeFile(final String s) {
    String result = s;
    try {
      URL url = new URL(s);
      String path = url.getPath();
      /* Wenn der Pfad einen Punkt enthält, bauen wir die URL neu, ohne die Datei: */
      if (path.contains(".")) {
        result = url.getProtocol() + "://" + url.getHost()
            + path.substring(0, path.lastIndexOf("/") + 1);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return result;
  }

  /* Wir normalisieren, wenn die URL nicht sowieso absolut ist: */
  private static String normalized(final String url, final String link) {
    if (link.startsWith("http://")) {
      return link;
    } else {
      return url + fixSlashes(url, link);
    }
  }

  /* Behandlung von überflüssigen oder fehlenden Slashes: */
  private static String fixSlashes(final String url, final String link) {
    String result = link;
    if (url.endsWith("/") && link.startsWith("/")) {
      result = link.substring(1);
    }
    if (!url.endsWith("/") && !link.startsWith("/")) {
      result = "/" + link;
    }
    return result;
  }

  /* Ausschliessen von verbotenen Links, mithilfe der jrobotx Bibliothek: */
  private static Set<String> allowed(final Set<String> normalized) throws MalformedURLException {
    /*
     * Wir initialisieren das von jrobotx verwendete log4j-System (wenn wir detaillierte Meldungen,
     * z.B. über fehlende robots.txt-Dateien sehen wollen):
     */
    // Logger.getLogger(RobotExclusion.class).addAppender(new ConsoleAppender(new SimpleLayout()));
    RobotExclusion robots = new RobotExclusion();
    Set<String> result = new HashSet<String>();
    for (String link : normalized) {
      /* Wenn der Link für jeden (*) bot erlaubt ist: */
      if (robots.allows(new URL(link), "*")) {
        result.add(link);
      }
      /* Da das Abfragen der robots.txt auch wieder eine Anfrage an den Server bewirkt, warten: */
      try {
        final long time = 300;
        Thread.sleep(time);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
}
