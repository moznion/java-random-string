package net.moznion.random.string;

import lombok.Getter;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
class RandomLetterPickers {
  private final RandomLetterPicker upperCase;
  private final RandomLetterPicker lowerCase;
  private final RandomLetterPicker digit;
  private final RandomLetterPicker symbol;
  private final RandomLetterPicker any;
  private final RandomLetterPicker salt;
  private final RandomLetterPicker binary;
  private final RandomLetterPicker word;
  private final RandomLetterPicker notWord;
  private final RandomLetterPicker notDigit;
  private final RandomLetterPicker space;

  public RandomLetterPickers(Random random) {
    upperCase = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(UpperCaseLetter.class)
        .build();

    lowerCase = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(LowerCaseLetter.class)
        .build();

    digit = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(DigitLetter.class)
        .build();

    symbol = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(SymbolLetter.class)
        .build();

    any = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(UpperCaseLetter.class)
        .addAllByEnum(LowerCaseLetter.class)
        .addAllByEnum(DigitLetter.class)
        .addAllByEnum(SymbolLetter.class)
        .build();

    salt = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(UpperCaseLetter.class)
        .addAllByEnum(LowerCaseLetter.class)
        .addAllByEnum(DigitLetter.class)
        .add(".")
        .add("/")
        .build();

    binary = RandomLetterPicker.builder()
        .setRandom(random)
        .addAll(IntStream.range(0, 255)
            .mapToObj(i -> Character.toString((char) i))
            .collect(Collectors.toList()))
        .build();

    word = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(UpperCaseLetter.class)
        .addAllByEnum(LowerCaseLetter.class)
        .addAllByEnum(DigitLetter.class)
        .add("_")
        .build();

    notWord = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(SymbolLetter.class)
        .remove("_")
        .build();

    notDigit = RandomLetterPicker.builder()
        .setRandom(random)
        .addAllByEnum(UpperCaseLetter.class)
        .addAllByEnum(LowerCaseLetter.class)
        .addAllByEnum(SymbolLetter.class)
        .build();

    space = RandomLetterPicker.builder()
        .setRandom(random)
        .add(" ")
        .add("\t")
        .build();
  }
}
