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

public class RandomStringGenerator {
  private final int maxRandomNum;
  private final Map<String, RandomLetterPicker> definedPickers;

  private static final Random RANDOM = new Random();

  public RandomStringGenerator() {
    this(10);
  }

  public RandomStringGenerator(int maxRandomNum) {
    this.maxRandomNum = maxRandomNum;
    this.definedPickers = new HashMap<>();
  }

  public String generateByPattern(final String pattern) {
    return Arrays.stream(pattern.split("")).map(patternChar -> {
      RandomLetterPicker picker;
      switch (patternChar) {
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
          throw new RuntimeException(); // TODO write description
      }
      return picker.pickRandomLetter();
    }).collect(Collectors.joining());
  }

  public String generateByRegex(final String regex) {
    String expanded = expandQuantifiers(regex);

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
            throw new RuntimeException(); // TODO write description
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
                  throw new RuntimeException(); // TODO write description
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
                  throw new RuntimeException(); // TODO write description
                }
                for (int k = beginCode; k <= endCode; k++) {
                  definedPickerBuilder.add(String.valueOf((char) k));
                }
              }
              definedPickers.put(key, definedPickerBuilder.build());
              picker = definedPickers.get(key);
            }
          } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(); // TODO write description
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
