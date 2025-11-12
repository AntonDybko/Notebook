package com.antond.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {

  public static Map<String, Long> calculateWordStats(String text) {
    if (text == null || text.trim().isEmpty()) {
      return Map.of();
    }

    return Pattern.compile("\\W+")
        .splitAsStream(text)
        .filter(word -> !word.isEmpty())
        .map(String::toLowerCase)
        .collect(Collectors.groupingBy(
            word -> word,
            LinkedHashMap::new,
            Collectors.counting()
        ));
  }
}
