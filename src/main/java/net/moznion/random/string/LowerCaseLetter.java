package net.moznion.random.string;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class LowerCaseLetter implements Letter {
  @Getter
  private static final List<String> VALUES = Collections.unmodifiableList(Arrays.asList(
      "a", "b", "c", "d", "e", "f", "g", "h",
      "i", "j", "k", "l", "m", "n", "o", "p",
      "q", "r", "s", "t", "u", "v", "w", "x",
      "y", "z"));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  @Override
  public String getRandomLetter() {
    return VALUES.get(RANDOM.nextInt(SIZE)).toString();
  }
}
