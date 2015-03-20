package net.moznion.random.string;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RandomStringGenerator {
  private static final Letter UPPER_CASE = new UpperCaseLetter();
  private static final Letter LOWER_CASE = new LowerCaseLetter();
  private static final Letter NUMERIC = new NumericLetter();
  private static final Letter SYMBOL = new SymbolLetter();
  private static final Letter ASCII = new ASCIILetter();
  private static final Letter SALT = new SaltLetter();
  private static final Letter BINARY = new BinaryLetter();

  public static String generateByPattern(final String pattern) {
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
          throw new RuntimeException();
      }
      return letter.getRandomLetter();
    }).collect(Collectors.joining());
  }
}
