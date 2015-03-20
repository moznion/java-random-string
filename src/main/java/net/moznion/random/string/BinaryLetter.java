package net.moznion.random.string;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class BinaryLetter implements Letter {
  @Getter
  private static final List<String> VALUES;
  private static final int SIZE;
  private static final Random RANDOM = new Random();

  static {
    VALUES = Collections.unmodifiableList(
        IntStream.range(0, 255).mapToObj(i -> {
          return Character.toString((char) i);
        }).collect(Collectors.toList()));
    SIZE = VALUES.size();
  }

  @Override
  public String getRandomLetter() {
    return VALUES.get(RANDOM.nextInt(SIZE)).toString();
  }
}
