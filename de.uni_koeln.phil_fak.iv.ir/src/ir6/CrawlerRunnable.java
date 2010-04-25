package ir6;

import java.util.Set;

/*
 * Ein Runnable, die Einheit dessen, was parallel zu tun ist. In unserem Fall heisst das: Von einer
 * Ausgangs-URL bis zur gewünschten Tiefe crawlen. Wir wollen nicht jeden Link parallel verfolgen,
 * da die meisten Links auf dem gleiche Host liegen und dies zur Folge hätte, dass aus vielen (bei
 * spiegel.de z.B. mehrere hundert) Threads gleichzeit Anfragen an einen host gingen, was nicht
 * höflich wäre und auch nicht funktionieren würde.
 */
/**
 * A crawler runnable that crawls from a given starting point.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class CrawlerRunnable implements Runnable {

  private int depth;
  private String url;
  private Set<WebDocument> result;

  /**
   * @param result The result list to add the crawled documents to
   * @param url The starting URL
   * @param depth The depth to crawl (0: starting URL only, 1: follow links on starting URL, etc.)
   */
  public CrawlerRunnable(final Set<WebDocument> result, final String url, final int depth) {
    this.result = result;
    this.url = url;
    this.depth = depth;
  }

  @Override
  /*
   * Die Einstiegsmethode: wird vom ExecutorService aufgerufen
   */
  public void run() {
    crawl(url, 0);
  }

  /*
   * Die rekursive crawling-Methode: parst das Dokument an der angegebenen URL, fügt das Ergebnis
   * der Collection hinzu und wenn i kleiner als das Level-Limit ist, ruft sie sich selbst mit allen
   * ausgehenden Links des Dokuments an der angegebenen URL auf.
   */
  private void crawl(final String url, final int current) {
    WebDocument doc = Parser.parse(url);
    result.add(doc);
    System.out.println("Crawled: " + doc);
    /* Ein Mindestmass an Verzögerung: */
    final long time = 300;
    delay(time);
    if (current < depth) {
      Set<String> links = doc.getLinks();
      for (String link : links) {
        crawl(link, current + 1);
      }
    }
  }

  private static void delay(final long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
