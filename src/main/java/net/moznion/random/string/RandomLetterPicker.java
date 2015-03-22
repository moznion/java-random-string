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

    public Builder() {
      letters = new ArrayList<>();
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

    public RandomLetterPicker build() {
      return new RandomLetterPicker(letters);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private RandomLetterPicker(List<String> letters) {
    this.letters = Collections.unmodifiableList(letters);

    size = letters.size();
    random = new Random();
  }

  public String getRandomLetter() {
    return letters.get(random.nextInt(size));
  }
}
