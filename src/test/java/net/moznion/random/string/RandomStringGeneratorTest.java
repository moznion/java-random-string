package net.moznion.random.string;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.regex.Pattern;

public class RandomStringGeneratorTest {
  @Test
  public void shouldGenerateRandomStringFromPattern() {
    RandomStringGenerator generator = new RandomStringGenerator();
    String randomString = generator.generateByPattern("cCn!.sb");
    Pattern patternToProve =
        Pattern.compile("^[a-z][A-Z][0-9][~`!@$%^&*()\\-_+={}\\[\\]|\\\\:;\"'.<>?/#,]"
            + "[a-zA-Z0-9~`!@$%^&*()\\-_+={}\\[\\]|\\\\:;\"'.<>?/#,]"
            + "[A-Za-z0-9./].$");
    assertTrue(patternToProve.matcher(randomString).find());
  }

  @Test(expected = RuntimeException.class)
  public void shouldOccurExceptionWhenItIsWithInvalidPatternChar() {
    new RandomStringGenerator().generateByPattern("cCn?.sb");
  }

  @Test
  public void shouldGenerateRandomStringFromRegex() {
    RandomStringGenerator generator = new RandomStringGenerator();
    String randomString = generator.generateByRegex("\\w+\\d*\\W\\D{0,3}a\\{0,3}.\\s\\S[0-9][a-zA-Z]X");
    Pattern patternToProve =
        Pattern.compile("^[a-zA-Z0-9_]+[0-9]*[~`!@$%^&*()\\-+={}\\[\\]|\\\\:;\"'.<>?/#,]"
            + "[a-zA-Z0-9~`!@$%^&*()\\-_+={}\\[\\]|\\\\:;\"'.<>?/#,]{0,3}"
            + "a\\{0,3}.[ \t].[0-9][a-zA-Z]X$");
    assertTrue(patternToProve.matcher(randomString).find());
  }

  @Test
  public void shouldIgnoreInvalidRange() {
    RandomStringGenerator generator = new RandomStringGenerator();
    {
      String randomString = generator.generateByRegex("a{-1,10}b{foo}");
      assertTrue(randomString.equals("a{-1,10}b{foo}"));
    }
    {
      String randomString = generator.generateByRegex("a{");
      assertTrue(randomString.equals("a{"));
    }
  }

  @Test(expected = RuntimeException.class)
  public void shouldOccurExceptionWhenExistsInvalidRange() {
    new RandomStringGenerator().generateByRegex("a{5,0}");
  }

  @Test(expected = RuntimeException.class)
  public void shouldOccurExceptionWhenExistsInvalidEscapeCharacter() {
    new RandomStringGenerator().generateByRegex("foo\\");
  }
}
