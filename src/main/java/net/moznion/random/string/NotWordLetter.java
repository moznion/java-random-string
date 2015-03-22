package net.moznion.random.string;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NotWordLetter implements Letter {
  @Getter
  private static final List<String> VALUES;
  private static final int SIZE;
  private static final Random RANDOM = new Random();

  static {
    List<String> stringList = new ArrayList<>();
    stringList.addAll(SymbolLetter.getVALUES());
    stringList.addAll(SymbolLetter.getVALUES().stream().filter(c -> {
      return c != "_";
    }).collect(Collectors.toList()));

    VALUES = Collections.unmodifiableList(stringList);
    SIZE = VALUES.size();
  }

  @Override
  public String getRandomLetter() {
    return VALUES.get(RANDOM.nextInt(SIZE)).toString();
  }
}
