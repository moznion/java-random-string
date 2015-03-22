random-string [![Build Status](https://travis-ci.org/moznion/java-random-string.svg)](https://travis-ci.org/moznion/java-random-string) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.moznion/random-string/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.moznion/random-string) [![javadoc.io](https://javadocio-badges.herokuapp.com/net.moznion/random-string/badge.svg)](https://javadocio-badges.herokuapp.com/net.moznion/random-string)
=============

Generate random strings based on a pattern.

Synopsis
---

### From pattern

```java
RandomStringGenerator generator = new RandomStringGenerator();

// generates random string (e.g. "aB4@X.Ç")
String randomString = generator.generateFromPattern("cCn!.sb");
```

### From regex

```java
RandomStringGenerator generator = new RandomStringGenerator();

// generates random string (e.g. "a5B123 18X")
String randomString = generator.generateByRegex("\\w+\\d*\\s[0-9]{0,3}X");
```

Description
--

Generate random string based on a patter.
This library is port of [String::Random](https://metacpan.org/pod/String::Random) from Perl to Java.

Methods
--

Please refer to javadoc.

[![javadoc.io](https://javadocio-badges.herokuapp.com/net.moznion/random-string/badge.svg)](https://javadocio-badges.herokuapp.com/net.moznion/random-string)

Author
--

moznion (<moznion@gmail.com>)

License
--

```
The MIT License (MIT)
Copyright © 2015 moznion, http://moznion.net/ <moznion@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

