package net.moznion.random.string;

import net.moznion.random.string.RandomLetterPicker.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Generator of random string.
 * 
 * @author moznion
 *
 */
public class RandomStringGenerator {
  private int numOfUpperLimit;
  private final Map<String, RandomLetterPicker> definedPickers;

  private static final Random RANDOM = new Random();

  /**
   * Instantiate generator with number of upper limit for regex quantifiers,
   * for example {@code *}, {@code +} and etc.
   * 
   * @param numOfUpperLimit Number of upper limit for quantifiers
   */
  public RandomStringGenerator(int numOfUpperLimit) {
    this.numOfUpperLimit = numOfUpperLimit;
    this.definedPickers = new HashMap<>();
  }

  /**
   * Instantiate generator with default number of upper limit for regex
   * quantifiers, for example {@code *}, {@code +} and etc (default value: 10).
   */
  public RandomStringGenerator() {
    this(10);
  }

  /**
   * Generate random string from pattern.
   * 
   * <p>
   * You can use following characters as pattern.
   * <ul>
   * <li>{@code c} : Any Latin lower-case character</li>
   * <li>{@code C} : Any Latin upper-case character</li>
   * <li>{@code n} : Any digit {@code [0-9]}</li>
   * <li>{@code !} : A symbol character {@code [~`!@$%^&*()-_+= []|\:;"'.<>?/#,]}</li>
   * <li>{@code .} : Any of the above</li>
   * <li>{@code s} : A "salt" character {@code [A-Za-z0-9./]}</li>
   * <li>{@code b} : An ASCIII character which has code from 0 to 255</li>
   * </ul>
   * 
   * <p>
   * e.g.
   * 
   * <pre>
   * <code>
   * RandomStringGenerator generator = new RandomStringGenerator();
   * 
   * // generates random string (e.g. "aB4@X.Ç")
   * String randomString = generator.generateFromPattern(&quot;cCn!.sb&quot;);
   * </code>
   * </pre>
   * 
   * @param pattern Pattern string
   * @return Random string which is generated according to pattern
   */
  public String generateFromPattern(final String pattern) {
    return Arrays.stream(pattern.split("")).map(patternCharacter -> {
      RandomLetterPicker picker;
      switch (patternCharacter) {
        case "c":
          picker = RandomLetterPickers.LOWER_CASE.getPicker();
          break;
        case "C":
          picker = RandomLetterPickers.UPPER_CASE.getPicker();
          break;
        case "n":
          picker = RandomLetterPickers.DIGIT.getPicker();
          break;
        case "!":
          picker = RandomLetterPickers.SYMBOL.getPicker();
          break;
        case ".":
          picker = RandomLetterPickers.ANY.getPicker();
          break;
        case "s":
          picker = RandomLetterPickers.SALT.getPicker();
          break;
        case "b":
          picker = RandomLetterPickers.BINARY.getPicker();
          break;
        default:
          throw new RuntimeException("Detected invalid pattern character: " + patternCharacter);
      }
      return picker.pickRandomLetter();
    }).collect(Collectors.joining());
  }

  /**
   * Generate random string from regular expression.
   * 
   * <p>
   * You can use following meta characters as regex.
   * <ul>
   * <li>{@code \w} : Alphanumeric + "_" {@code [A-Za-z0-9_]}</li>
   * <li>{@code \d} : Digits {@code [0-9]}</li>
   * <li>{@code \W} : Printable characters other than those in \w</li>
   * <li>{@code \D} : Printable characters other than those in \d</li>
   * <li>{@code \s} : Whitespace characters {@code [ \t]}</li>
   * <li>{@code \S} : Printable characters</li>
   * <li>{@code .} : Printable characters</li>
   * <li>{@code []} : Character classes (Example of usage {@code [a-zA-Z]})</li>
   * <li>{@code : Repetition</li>
   * <li>{@code *} : Same as {0,}</li>
   * <li>{@code ?} : Same as {0,1}</li>
   * <li>{@code +} : Same as {1,}</li>
   * </ul>
   * 
   * <p>
   * e.g.
   * 
   * <pre>
   * <code>
   * RandomStringGenerator generator = new RandomStringGenerator();
   * 
   * // generates random string (e.g. "a5B123 18X")
   * String randomString = generator.generateByRegex("\\w+\\d*\\s[0-9]{0,3}X");
   * </code>
   * </pre>
   * 
   * @param regex Pattern based on regular expression
   * @return Random String
   */
  public String generateByRegex(final String regex) {
    String expanded = normalizeQuantifiers(regex);

    final String[] regexCharacters = expanded.split("");
    final int length = regexCharacters.length;

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      String character = regexCharacters[i];
      RandomLetterPicker picker = null;
      String candidateCharacter = null;
      switch (character) {
        case "\\":
          try {
            character = regexCharacters[++i];
          } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Detected invalid escape character");
          }

          switch (character) {
            case "w":
              picker = RandomLetterPickers.WORD.getPicker();
              break;
            case "d":
              picker = RandomLetterPickers.DIGIT.getPicker();
              break;
            case "W":
              picker = RandomLetterPickers.NOT_WORD.getPicker();
              break;
            case "D":
              picker = RandomLetterPickers.NOT_DIGIT.getPicker();
              break;
            case "s":
              picker = RandomLetterPickers.SPACE.getPicker();
              break;
            case "S":
              picker = RandomLetterPickers.ANY.getPicker();
              break;
            default:
              candidateCharacter = character;
          }
          break;
        case "[":
          List<String> buffer = new ArrayList<>();
          try {
            String key = "";
            while (!(character = regexCharacters[++i]).equals("]")) {
              // Scan string which is in brackets to determine name of key and code range
              if (character.equals("-") && !buffer.isEmpty()) {
                String beginCharacter = buffer.get(buffer.size() - 1);
                String endCharacter = regexCharacters[++i];
                key += beginCharacter + "-" + endCharacter;
                buffer.add(endCharacter);
              } else {
                if (String.valueOf(character).matches("\\W")) {
                  throw new RuntimeException("'" + character + "'"
                      + "will be treated literally inside []");
                }
                buffer.add(character);
              }
            }

            if (definedPickers.get(key) == null) {
              // build random letter picker according to determined range at above
              Builder definedPickerBuilder = RandomLetterPicker.builder();
              int bufferSize = buffer.size();
              for (int j = 0; j < bufferSize; j += 2) {
                int beginCode = (int) buffer.get(j).charAt(0);
                int endCode = (int) buffer.get(j + 1).charAt(0);
                if (beginCode > endCode) {
                  throw new RuntimeException("Detected invalid character range: " + character);
                }
                for (int k = beginCode; k <= endCode; k++) {
                  definedPickerBuilder.add(String.valueOf((char) k));
                }
              }
              definedPickers.put(key, definedPickerBuilder.build());
              picker = definedPickers.get(key);
            }
          } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Occurs parsing error");
          }
          break;
        case ".":
          picker = RandomLetterPickers.ANY.getPicker();
          break;
        default:
          candidateCharacter = character;
      }

      int repetitionNum = 1;
      if (i + 1 < length) {
        String nextCharacter = regexCharacters[i + 1];
        if (nextCharacter.equals("{")) {
          int j = i + 1;
          StringBuilder sbForQuantifier = new StringBuilder();
          try {
            while (!(nextCharacter = regexCharacters[++j]).equals("}")) {
              sbForQuantifier.append(nextCharacter);
            }
            try {
              repetitionNum = Integer.parseInt(sbForQuantifier.toString(), 10);
              if (repetitionNum >= 0) {
                i += j - 1;
              }
            } catch (RuntimeException e) {
              // do nothing
            }
          } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing
          }
        }
      }

      if (picker != null) {
        for (int j = 0; j < repetitionNum; j++) {
          sb.append(picker.pickRandomLetter());
        }
      } else if (candidateCharacter != null) {
        for (int j = 0; j < repetitionNum; j++) {
          sb.append(candidateCharacter);
        }
      } else {
        throw new RuntimeException("Occurs parsing error");
      }
    }

    return sb.toString();
  }

  /**
   * Get number of upper limit for regex quantifiers,
   * for example {@code *}, {@code +} and etc.
   * 
   * @return Number of upper limit for quantifiers
   */
  public int getNumOfUpperLimit() {
    return numOfUpperLimit;
  }

  /**
   * Set number of upper limit for regex quantifiers,
   * for example {@code *}, {@code +} and etc.
   * 
   * @param numOfUpperLimit Number of upper limit for quantifiers
   */
  public void setNumOfUpperLimit(int numOfUpperLimit) {
    this.numOfUpperLimit = numOfUpperLimit;
  }

  // for repetition quantifier, e.g. {1,4}
  private static final Pattern REPETITION_QUANTIFIER_RE =
      Pattern.compile("([^\\\\])\\{([0-9]+),([0-9]+)\\}");
  private static final Pattern ASTERISK_QUANTIFIER_RE = Pattern.compile("([^\\\\])\\*");
  private static final Pattern PLUS_QUANTIFIER_RE = Pattern.compile("([^\\\\])\\+");
  private static final Pattern QUESTION_QUANTIFIER_RE = Pattern.compile("([^\\\\])\\?");

  private String normalizeQuantifiers(final String regex) {
    String expanded = regex;

    Matcher repetitionMatcher = REPETITION_QUANTIFIER_RE.matcher(expanded);
    while (repetitionMatcher.find()) {
      int start = Integer.parseInt(repetitionMatcher.group(2), 10);
      int end = Integer.parseInt(repetitionMatcher.group(3), 10);
      if (end - start < 0) {
        throw new RuntimeException("Detected invalid quantifier: " + "{" + start + "," + end + "}");
      }
      expanded =
          repetitionMatcher.replaceFirst(repetitionMatcher.group(1) + "{"
              + getRandomNumAsString(start, end) + "}");
      repetitionMatcher = REPETITION_QUANTIFIER_RE.matcher(expanded);
    }

    Matcher asteriskMatcher = ASTERISK_QUANTIFIER_RE.matcher(expanded);
    while (asteriskMatcher.find()) {
      expanded =
          asteriskMatcher.replaceFirst(asteriskMatcher.group(1) + "{"
              + getRandomNumAsString(0, numOfUpperLimit) + "}");
      asteriskMatcher = ASTERISK_QUANTIFIER_RE.matcher(expanded);
    }
    //
    Matcher plusMatcher = PLUS_QUANTIFIER_RE.matcher(expanded);
    while (plusMatcher.find()) {
      expanded =
          plusMatcher.replaceFirst(plusMatcher.group(1) + "{"
              + getRandomNumAsString(1, numOfUpperLimit) + "}");
      plusMatcher = PLUS_QUANTIFIER_RE.matcher(expanded);
    }

    Matcher questionMatcher = QUESTION_QUANTIFIER_RE.matcher(expanded);
    while (questionMatcher.find()) {
      expanded =
          questionMatcher.replaceFirst(questionMatcher.group(1) + "{"
              + getRandomNumAsString(0, 1) + "}");
      questionMatcher = QUESTION_QUANTIFIER_RE.matcher(expanded);
    }

    return expanded;
  }

  private String getRandomNumAsString(final int start, final int end) {
    return Integer.toString(RANDOM.nextInt(end + 1) + start, 10);
  }
}
