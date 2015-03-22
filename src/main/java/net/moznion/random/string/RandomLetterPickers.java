package net.moznion.random.string;

import lombok.Getter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

enum RandomLetterPickers {
  UPPER_CASE(RandomLetterPicker.builder()
      .addAllByEnum(UpperCaseLetter.class)
      .build()),

  LOWER_CASE(RandomLetterPicker.builder()
      .addAllByEnum(LowerCaseLetter.class)
      .build()),

  DIGIT(RandomLetterPicker.builder()
      .addAllByEnum(DigitLetter.class)
      .build()),

  SYMBOL(RandomLetterPicker.builder()
      .addAllByEnum(SymbolLetter.class)
      .build()),

  ANY(RandomLetterPicker.builder()
      .addAllByEnum(UpperCaseLetter.class)
      .addAllByEnum(LowerCaseLetter.class)
      .addAllByEnum(DigitLetter.class)
      .addAllByEnum(SymbolLetter.class)
      .build()),

  SALT(RandomLetterPicker.builder()
      .addAllByEnum(UpperCaseLetter.class)
      .addAllByEnum(LowerCaseLetter.class)
      .addAllByEnum(DigitLetter.class)
      .add(".")
      .add("/")
      .build()),

  BINARY(RandomLetterPicker.builder()
      .addAll(IntStream.range(0, 255)
          .mapToObj(i -> Character.toString((char) i))
          .collect(Collectors.toList()))
      .build()),

  WORD(RandomLetterPicker.builder()
      .addAllByEnum(UpperCaseLetter.class)
      .addAllByEnum(LowerCaseLetter.class)
      .addAllByEnum(DigitLetter.class)
      .add("_")
      .build()),

  NOT_WORD(RandomLetterPicker.builder()
      .addAllByEnum(SymbolLetter.class)
      .remove("_")
      .build()),

  NOT_DIGIT(RandomLetterPicker.builder()
      .addAllByEnum(UpperCaseLetter.class)
      .addAllByEnum(LowerCaseLetter.class)
      .addAllByEnum(SymbolLetter.class)
      .build()),

  SPACE(RandomLetterPicker.builder()
      .add(" ")
      .add("\t")
      .build());

  @Getter
  private RandomLetterPicker picker;

  private RandomLetterPickers(RandomLetterPicker picker) {
    this.picker = picker;
  }
}
