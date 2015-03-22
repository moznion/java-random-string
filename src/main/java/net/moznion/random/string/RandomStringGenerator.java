package net.moznion.random.string;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RandomStringGenerator {
  private final int maxRandomNum;

  private static final Random RANDOM = new Random();

  private static final Letter UPPER_CASE = new UpperCaseLetter();
  private static final Letter LOWER_CASE = new LowerCaseLetter();
  private static final Letter NUMERIC = new NumericLetter();
  private static final Letter SYMBOL = new SymbolLetter();
  private static final Letter ASCII = new ASCIILetter();
  private static final Letter SALT = new SaltLetter();
  private static final Letter BINARY = new BinaryLetter();
  private static final Letter WORD = new WordLetter();
  private static final Letter NOT_WORD = new NotWordLetter();
  private static final Letter NOT_NUMERIC = new NotNumericLetter();

  public RandomStringGenerator() {
    this(10);
  }

  public RandomStringGenerator(int maxRandomNum) {
    this.maxRandomNum = maxRandomNum;
  }

  public String generateByPattern(final String pattern) {
    return Arrays.stream(pattern.split("")).map(patternChar -> {
      Letter letter;
      switch (patternChar) {
        case "c":
          letter = LOWER_CASE;
          break;
        case "C":
          letter = UPPER_CASE;
          break;
        case "n":
          letter = NUMERIC;
          break;
        case "!":
          letter = SYMBOL;
          break;
        case ".":
          letter = ASCII;
          break;
        case "s":
          letter = SALT;
          break;
        case "b":
          letter = BINARY;
          break;
        default:
          throw new RuntimeException(); // TODO write description
      }
      return letter.getRandomLetter();
    }).collect(Collectors.joining());
  }

  public String generateByRegex(final String regex) {
    String expanded = expandQuantifiers(regex);

    final String[] regexCharacters = expanded.split("");
    final int length = regexCharacters.length;

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      String character = regexCharacters[i];
      Letter candidate = null;
      String candidateCharacter = null;
      switch (character) {
        case "\\":
          try {
            character = regexCharacters[++i];
          } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(); // TODO write description
          }

          switch (character) {
            case "w":
              candidate = WORD;
              break;
            case "d":
              candidate = NUMERIC;
              break;
            case "W":
              candidate = NOT_WORD;
              break;
            case "D":
              candidate = NOT_NUMERIC;
              break;
            default:
              candidateCharacter = character;
          }
          break;
        // case "[":
        // break;
        case ".":
          candidate = ASCII;
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

      if (candidate != null) {
        for (int j = 0; j < repetitionNum; j++) {
          sb.append(candidate.getRandomLetter());
        }
      } else if (candidateCharacter != null) {
        for (int j = 0; j < repetitionNum; j++) {
          sb.append(candidateCharacter);
        }
      } else {
        throw new RuntimeException();
      }
    }

    return sb.toString();
  }

  // for repetition quantifier, e.g. {1,4}
  private static final Pattern REPETITION_QUANTIFIER_RE =
      Pattern.compile("([^\\\\])\\{([0-9]+),([0-9]+)\\}");
  private static final Pattern ASTERISK_QUANTIFIER_RE = Pattern.compile("([^\\\\])\\*");
  private static final Pattern PLUS_QUANTIFIER_RE = Pattern.compile("([^\\\\])\\+");
  private static final Pattern QUESTION_QUANTIFIER_RE = Pattern.compile("([^\\\\])\\?");

  private String expandQuantifiers(final String regex) {
    String expanded = regex;

    Matcher repetitionMatcher = REPETITION_QUANTIFIER_RE.matcher(expanded);
    while (repetitionMatcher.find()) {
      int start = Integer.parseInt(repetitionMatcher.group(2), 10);
      int end = Integer.parseInt(repetitionMatcher.group(3), 10);
      if (end - start < 0) {
        throw new RuntimeException(); // TODO write description
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
              + getRandomNumAsString(0, maxRandomNum) + "}");
      asteriskMatcher = ASTERISK_QUANTIFIER_RE.matcher(expanded);
    }
    //
    Matcher plusMatcher = PLUS_QUANTIFIER_RE.matcher(expanded);
    while (plusMatcher.find()) {
      expanded =
          plusMatcher.replaceFirst(plusMatcher.group(1) + "{"
              + getRandomNumAsString(1, maxRandomNum) + "}");
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
