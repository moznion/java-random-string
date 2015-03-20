package net.moznion.random.string;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class SaltLetter implements Letter {
  @Getter
  private static final List<String> VALUES;
  private static final int SIZE;
  private static final Random RANDOM = new Random();

  static {
    List<String> stringList = new ArrayList<>();
    stringList.addAll(LowerCaseLetter.getVALUES());
    stringList.addAll(UpperCaseLetter.getVALUES());
    stringList.addAll(NumericLetter.getVALUES());
    stringList.addAll(Arrays.asList(".", "/"));
    VALUES = Collections.unmodifiableList(stringList);
    SIZE = VALUES.size();
  }

  @Override
  public String getRandomLetter() {
    return VALUES.get(RANDOM.nextInt(SIZE)).toString();
  }
}
