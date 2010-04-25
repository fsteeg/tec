package ir6;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Ein einfacher paralleler Crawler: beginnend mit einer Menge von URLs werden diese zu
 * Web-Dokumenten gecrawled und Links bis zum angegebenen Level gefolgt. Die Ergebnisse werden in
 * ein Set gepackt, das am Ende zurückgegeben wird.
 */
/**
 * A simple crawler that follows the seed concurrently.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Crawler {
  private Crawler() {} // enforce non-instantiability

  /**
   * @param seed The seed URLs
   * @param depth The crawling depth (0 means seed only, 1 follow all links on the seed pages, etc.)
   * @return The documents crawled from the given seed with the given depth
   */
  public static Set<WebDocument> crawl(final List<String> seed, final int depth) {
    long start = System.currentTimeMillis();
    /* Das Ergebnis des Crawling wird eine Menge von Web-Dokumenten sein. */
    /*
     * Um beim nebenläufigen Füllen keine Fehler zu produzieren (zwei Threads könnten gleichzeitig
     * identische Dokumente einfügen), wrappen wir unser Set in eine synchronisierte, d.h.
     * Thread-sichere Wrapper-Klasse, vgl. Naftalin & Wadler, 17.3
     */
    Set<WebDocument> result = Collections.synchronizedSet(new HashSet<WebDocument>());
    /*
     * Früher benutzte man zur parallelen Programmierung unter Java Threads direkt, sowohl als
     * Einheit der Arbeit, als auch um diese auszuführen. Diese beiden Dinge trennt man inzwischen:
     * Einheit für die Arbeit ist ein Runnable, zum Ausführen nimmt man einen executor service (vgl.
     * Effective Java, Second Edition, Kap. 10):
     */
    /*
     * Um hier etwa nicht parallel zu arbeiten würde man sich mit
     * "Executors.newSingleThreadExecutor()" einen anderen service holen, oder mit
     * "Executors.newFixedThreadPool(2)" eine, der nicht immer mehr Threads erzeugt sondern immer
     * mit 2 Threads arbeitet.
     */
    ExecutorService exec = Executors.newCachedThreadPool(); // newFixedThreadPool(1);
    for (String url : seed) {
      /*
       * Für jede Start-URL starten wir ein Runnable, diese werden dann parallel ausgeführt. Die
       * ausgehenden Links werden dann nicht wieder neu parallel ausgeführt, weil dies zu viele
       * Anfragen an einen einzgen host zu folge hätte (siehe Kommentar in CrawlRunnable)
       */
      exec.execute(new CrawlerRunnable(result, url, depth));
    }
    /* Dass der Executor irgendwann stoppen soll muss man ihm sagen: */
    exec.shutdown();
    /*
     * Jetzt sind wir am laufen... wenn wir jetzt nichts täten wären wir raus und die Threads laufen
     * weiter. Wir wollen aber am Ende was mit de Ergebnissen machen (später indexieren und darauf
     * suchen, jetzt erstmal nur ansehen wie viele Dokumente wir haben), also warten wir auf die
     * Vollendung der Arbeit (und zwar mit grosszügigem timeout):
     */
    try {
      final int timeout = 5;
      boolean terminated = exec.awaitTermination(timeout, TimeUnit.DAYS);
      /*
       * Hätten wir einen kürzeren Timeout angegeben, wäre der resultierende boolean false, wenn
       * aufgrund des Timeouts abgebrochen worden wäre (siehe JavaDoc von awaitTermination):
       */
      System.out.println("Done in time limit: " + terminated);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    long took = System.currentTimeMillis() - start;
    final int milli = 1000;
    System.out.println(String.format("Crawling %s documents took %s ms. (~%s s.)", result.size(),
        took, took / milli));
    return result;
  }

}
