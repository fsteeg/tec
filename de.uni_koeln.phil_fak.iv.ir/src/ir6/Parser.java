package ir6;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/*
 * Der Parser übernimmt für uns die Umwandlung einer URL in ein WebDocument, das Inhalt und
 * ausgehende Links der Seite an der URL enthält.
 */
/**
 * A parser that transforms URLs into document representations using the error-correcting Neko
 * parser, which is based on Xerces.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Parser {
  private Parser() {}// enforce non-instantiability

  private static Set<String> links;
  private static StringBuilder builder;

  /**
   * @param url The URL of the page to parse
   * @return A document representation for the site at the given URL
   */
  public static WebDocument parse(final String url) {
    /*
     * Als Parser verwenden wir NekoHTML, einen fehlerkorrigierenden Parser
     * (http://nekohtml.sourceforge.net/) der auf Xerces aufbaut:
     */
    DOMParser parser = new DOMParser();
    try {
      /* Dem Neko-Parser können wir einfach die URL als String übergeben: */
      parser.parse(url);
      /* Wir instanziieren die zu fülleneden Werte: */
      builder = new StringBuilder();
      links = new HashSet<String>();
      /* Und beginnen die Verarbeitung mit dem ersten Element: */
      Node root = parser.getDocument().getFirstChild();
      process(root);
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    /* Am Ende erzeugen wir aus den Werten unser Dokument-Objekt: */
    return new WebDocument(url, builder.toString().trim(), links);
  }

  private static void process(final Node node) throws MalformedURLException {
    /*
     * Wir ermitteln die Elemente über ihre Namen. Man könnte auch mit instanceof testen ob es etwa
     * ein HTMLParagraphElement ist, allerdings ist das weniger zuverlässig, da etwa XHTML-Dokumente
     * nicht aus solchen (HTML...), sondern aus anderen Elementen (in einem anderen Namensraum als
     * HTML-Elemente...) bestehen.
     */
    String elementName = node.getNodeName().toLowerCase().trim();
    /* Inhalt ist für uns hier nur das, was in p-Tags steht: */
    if (elementName.equals("p")) {
      String text = node.getTextContent().trim();
      if (text.length() > 0) {
        builder.append(text).append("\n\n");
      }
    } else if (elementName.equals("a")) {
      /* Und die Links merken wir uns auch: */
      if (node.hasAttributes()) {
        /*
         * Wenn das a-Element ein "href"-Attribut hat, kommt dieses in die Liste der Links:
         */
        Node href = node.getAttributes().getNamedItem("href");
        if (href != null) {
          links.add(href.getNodeValue().trim());
        }
      }
    }
    /*
     * Jetzt ist der aktuelle Knoten abgearbeitet, jetzt kommen kommt der rekursive Aufruf für den
     * nächsten Knoten auf der gleichen Schatelungsebene (wenn es einen gibt):
     */
    Node sibling = node.getNextSibling();
    if (sibling != null) {
      process(sibling);
    }
    /*
     * Nachdem jeder je seinen Nachbarn aufgerufen hat, kommt jetzt das ganze mit den
     * untergeordneten:
     */
    Node child = node.getFirstChild();
    if (child != null) {
      process(child);
    }
  }

}
