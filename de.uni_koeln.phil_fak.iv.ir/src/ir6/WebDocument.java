package ir6;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/*
 * Das Ergebnis der Verarbeitung einer URL: Ein Web-Dokument mit der Herkunfts-URL (z.B. für
 * Relevanz-Gewichtungen), dem eigentlichen Inhalt als String und den ausgehenden Links (für
 * weitergehendes Crawling etc.). Hier haben wir die Links einfach im Dokument anstelle einer
 * eigenen Frontier mit front queues und back queues (siehe IR-Buch, Kap. 20)
 */
/**
 * Document representation of a website, consisting of the text, the URL and the outgoing links.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class WebDocument {

  private String text;
  private Set<String> links;
  private URL url;

  /**
   * @param url The URL this document represents
   * @param text The plain text of this document
   * @param links The outgoing links
   */
  public WebDocument(final String url, final String text, final Set<String> links) {
    if (url == null || text == null || links == null) {
      throw new IllegalArgumentException("Document parameters must not be null");
    }
    this.text = text;
    try {
      /* Die Links müssen überprüft werden: */
      this.links = LinkHelper.checked(url, links);
      this.url = new URL(url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  /*
   * Wir überschreiben equals und hashCode damit wir doppelte Dokumente vermeiden können, indem wir
   * einfach eine entsprechende Datenstruktur verwenden (hier ein Set).
   */

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof WebDocument)) {
      return false;
    }
    WebDocument that = (WebDocument) obj;
    return this.url.toString().equals(that.url.toString()) && this.text.equals(that.text)
        && this.links.equals(that.links);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    final int start = 17;
    int result = start;
    result = prime * result + url.toString().hashCode();
    result = prime * result + text.hashCode();
    result = prime * result + links.hashCode();
    return result;
  }

  /* Und wir überschreiben toString um hilfreiche Ausgaben zu bekommen: */
  @Override
  public String toString() {
    return String.format("WebDocument at %s with %s outgoing links and text size %s", url, links
        .size(), text.length());
  }

  /**
   * @return The text
   */
  public String getText() {
    return text;
  }

  /**
   * @return The outgoing links
   */
  public Set<String> getLinks() {
    return links;
  }

  /**
   * @return The URL
   */
  public URL getUrl() {
    return url;
  }
  
  

}
