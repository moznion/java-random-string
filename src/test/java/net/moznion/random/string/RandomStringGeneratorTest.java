package net.moznion.random.string;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.regex.Pattern;

public class RandomStringGeneratorTest {
  @Test
  public void shouldGenerateRandomStringFromPattern() {
    String randomString = RandomStringGenerator.generateByPattern("cCn!.sb");
    Pattern patternToProve =
        Pattern.compile("^[a-z][A-Z][0-9][~`!@$%^&*()\\-_+={}\\[\\]|\\\\:;\"'.<>?/#,]"
            + "(?:[a-z]|[A-Z]|[0-9]|[~`!@$%^&*()\\-_+={}\\[\\]|\\\\:;\"'.<>?/#,])"
            + "[A-Za-z0-9./].$");
    assertTrue(patternToProve.matcher(randomString).find());
  }

  @Test(expected = RuntimeException.class)
  public void shouldOccurExceptionWhenItIsWithInvalidPatternChar() {
    RandomStringGenerator.generateByPattern("cCn?.sb");
  }
}
