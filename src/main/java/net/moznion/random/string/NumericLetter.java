package net.moznion.random.string;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class NumericLetter implements Letter {
  @Getter
  private static final List<String> VALUES = Collections.unmodifiableList(Arrays.asList(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  @Override
  public String getRandomLetter() {
    return VALUES.get(RANDOM.nextInt(SIZE)).toString();
  }
}
