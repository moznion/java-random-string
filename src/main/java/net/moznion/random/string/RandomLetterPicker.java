package net.moznion.random.string;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

class RandomLetterPicker {
  private final List<String> letters;
  private final int size;
  private final Random random;

  @Getter
  public static class Builder {
    private List<String> letters;
    private Random random;

    public Builder() {
      letters = new ArrayList<>();
      random = null;
    }

    public <E extends Enum<E> & Letter> Builder addAllByEnum(Class<E> enumClass) {
      letters.addAll(Arrays.stream(enumClass.getEnumConstants())
          .map(e -> e.getLetter())
          .collect(Collectors.toList()));
      return this;
    }

    public Builder addAll(List<String> list) {
      letters.addAll(list);
      return this;
    }

    public Builder add(String letter) {
      letters.add(letter);
      return this;
    }

    public Builder remove(String remove) {
      letters = letters.stream()
          .filter(l -> !l.equals(remove))
          .collect(Collectors.toList());
      return this;
    }

    public Builder setRandom(Random random) {
      this.random = random;
      return this;
    }

    public RandomLetterPicker build() {
      return new RandomLetterPicker(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private RandomLetterPicker(Builder b) {
    letters = Collections.unmodifiableList(b.getLetters());
    if (b.getRandom() == null) {
      random = new Random();
    } else {
      random = b.getRandom();
    }

    size = letters.size();
  }

  public String pickRandomLetter() {
    return letters.get(random.nextInt(size));
  }
}
