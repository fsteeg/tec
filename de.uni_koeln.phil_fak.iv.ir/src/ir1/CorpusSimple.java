package ir1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Eine Klasse zu Repraesentation des Korpus und zum Zugriff auf die Werke und die Woerter des
 * Korpus. Im Konstruktor werden der Ort der Textdatei und das Muster, mit dem die einzelnen
 * Dokumente des Korpus getrennt werden sollen, uebergeben.
 */
/**
 * A simple corpus class representing a text and its tokenization into individual works.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class CorpusSimple {

  private String text;
  private List<String> works;

  /**
   * @param location The location of the file
   * @param regex The regex to split the content of the file into individual works
   */
  public CorpusSimple(final String location, final String regex) {
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
    this.works = Arrays.asList(text.split(regex));
  }

  /**
   * @return The works this corpus is made of
   */
  public List<String> getWorks() {
    return works;
  }

}
