package com.antond.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Utility service for text analysis and processing operations.
 * Provides methods for calculating various text statistics and metrics.
 */
@Service
public class TextUtils {

  /**
   * Calculates word frequency statistics from the provided text.
   * The method splits text into words using non-word character boundaries,
   * converts all words to lowercase for case-insensitive counting,
   * and returns a map of each unique word to its frequency count.
   * The results maintain insertion order using LinkedHashMap for consistent output.
   *
   * @param text the input text to analyze; if null or empty, returns an empty map
   * @return a LinkedHashMap where keys are unique lowercase words and values are
   *         frequency counts, preserving the order of first occurrence
   * @implNote This method uses regex "\\W+" to split on non-word characters,
   *           which includes punctuation, spaces, and other non-alphanumeric characters
   * @implSpec Empty strings resulting from the split are filtered out,
   *           and all words are normalized to lowercase before counting
   * Example:
   * Input: "note is just a note"
   * Output: {“note”: 2, “is”: 1, “just”: 1, “a”: 1}
   */
  public Map<String, Long> calculateWordStats(String text) {
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