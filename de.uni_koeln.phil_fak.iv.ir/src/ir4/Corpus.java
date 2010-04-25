package ir4;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Eine Klasse zu Repraesentation des Korpus und zum Zugriff auf die Werke und die Woerter des
 * Korpus. Im Konstruktor werden der Ort der Textdatei, das Muster, mit dem die einzelnen Dokumente
 * des Korpus getrennt werden sollen, sowie das Trennelement von Dokument und Titel uebergeben.
 */
/**
 * A simple corpus class representing a text and its tokenization into individual works.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Corpus {

  private String text;
  private List<Document> works;

  /**
   * @param location The location of the file
   * @param titleDelimiter The regex to split the title from a work's body
   * @param workDelimiter The regex to split the content of the file into individual works
   */
  public Corpus(final String location, final String workDelimiter, final String titleDelimiter) {
    StringBuilder builder = new StringBuilder();
    try {
      Scanner scanner = new Scanner(new File(location));
      while (scanner.hasNextLine()) {
        /* Wir lesen Zeilen und fügen Zeilenenden wieder ein: */
        builder.append(scanner.nextLine()).append("\n");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    this.text = builder.toString();
    this.works = new ArrayList<Document>();
    List<String> workTexts = Arrays.asList(text.split(workDelimiter));
    for (String w : workTexts.subList(1, workTexts.size())) {
      /* Wir verwenden das übergebene Trennelement um den Titel vom Text zu trennen: */
      String title = (w.trim().substring(0, w.trim().indexOf(titleDelimiter))).trim();
      works.add(new Document(title, w));
    }
  }

  /**
   * @return The works this corpus is made of
   */
  public List<Document> getWorks() {
    return works;
  }

}
