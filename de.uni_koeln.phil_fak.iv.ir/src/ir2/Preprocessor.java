package ir2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Einfacher Preprocessor der Spezialfälle extrahiert und den Rest simpel splittet und sortierte
 * Types zurückgibt.
 */
/**
 * A preprocessor based on regular expressions: first extracts custom patterns, then splits on a
 * given delimiter.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class Preprocessor {

  /*
   * Verfügbare Musterbeschreibungen für zu extrahierenden Formate. Der Vorteil der Verwendung einer
   * Enum (anstelle von Konstanten) liegt darin, dass so alle in dieser Enum enthaltenen Muster im
   * Default-Konstruktor des Preprocessor ausgelesen werden können.
   */
  enum ExtractionPattern {
    /*
     * Regulärer Ausdruck für einfache Telefonnummern (0221-4701751), Versionsnummern (8.04),
     * Geldbeträge (3,50) und Uhrzeiten (15:15).
     */
    COMPOUND_NUMBER("\\d+[-.,:]\\d+"),
    /*
     * Regulärer Ausdruck für einfache Zahlen, wenn man die etwa von obigen unterscheiden will.
     */
    SIMPLE_NUMBER("\\d+"),
    /*
     * Emailadressen für einige Domains, mit Unterstützung von Punkten im Domainnamen (wie in
     * fsteeg@spinfo.uni-koeln.de), als Beispiel was man sonst so mit regulären Ausdrücken machen
     * kann.
     */
    EMAIL("[^@\\s]+@.+?\\.(de|com|eu|org|net)");

    private String regex;

    /**
     * @return The regular expression for this pattern
     */
    public String getRegex() {
      return regex;
    }

    ExtractionPattern(final String regularExpression) {
      this.regex = regularExpression;
    }
  }

  /*
   * Ein Unicode-wirksamer Ausdruck für "Nicht-Buchstabe", der auch Umlaute berücksichtigt; die
   * einfache (ASCII) Version ist: "\\W"
   */
  private static final String UNICODE_AWARE_DELIMITER = "[^\\p{L}]";
  private List<ExtractionPattern> specialCases;
  private String delimiter;

  /*
   * Konstruktor mit Argumenten, um speziell konfigurierte Vorverarbeitung zu ermöglichen (z.B.
   * nicht alle Muster extrahieren, einen speziellen Delimiter verwenden).
   */
  /**
   * @param specialCases The extraction patterns to be treated as special cases
   * @param tokenDelimiter The token delimiter to be used to split the text after the special cases
   *        are extracted
   */
  Preprocessor(final List<ExtractionPattern> specialCases, final String tokenDelimiter) {
    this.specialCases = specialCases;
    this.delimiter = tokenDelimiter;
  }

  /**
   * Creates a preprocessor configured to use default patterns and delim.
   */
  public Preprocessor() {
    specialCases = new ArrayList<ExtractionPattern>();
    /*
     * Im no-arg constructor verwenden wir alle patterns (einer der Vorteile von enums: man kann
     * darüber iterieren, im Gegensatz zu Konstanten):
     */
    for (ExtractionPattern p : ExtractionPattern.values()) {
      specialCases.add(p);
    }
    delimiter = UNICODE_AWARE_DELIMITER;
  }

  /**
   * @param input The text to process
   * @return A list of tokens
   */
  public List<String> tokenize(final String input) {
    /* Einheitliches lower-casing, wie in der Theorie besprochen: */
    String text = input.toLowerCase();
    List<String> result = new ArrayList<String>(); // tokens
    for (ExtractionPattern p : specialCases) {
      Pattern pattern = Pattern.compile(p.regex);
      Matcher matcher = pattern.matcher(text);
      while (matcher.find()) {
        String group = matcher.group();
        result.add(group);
        /*
         * Hier hatte sich der Fehler eingeschlichen weshalb kurz vor Ende das Entfernen der
         * Spaezialfälle nicht geklappt hat: wir hatten nur `string.replace(group, "")` gesagt, da
         * Strings aber immutable sind ändert diese Methode den String ja nicht, sondern gibt einen
         * neuen String zurück, d.h. wir müssen sagen:
         */
        text = text.replace(group, "");
        /*
         * Hier muss man aufpassen: replaceAll nimmt einen regulären Ausdruck als erstes Argument.
         * Das ist nicht was wir wollen, weil die extrahierten Muster als reguläre Ausdrücke wieder
         * etwas anderes bedeuten, oder erst gar keine gültigen regulären Ausdrücke sind. Daher
         * nehmen wir hier die replace-Methode, die einen einfachen String sucht und ersetzt.
         */
      }
    }
    /* Den Rest splitten wir normal, und filtern leere Strings: */
    List<String> list = Arrays.asList(text.split(delimiter));
    for (String s : list) {
      if (s.trim().length() > 0) {
        result.add(s.trim());
      }
    }
    return result;
  }

}
