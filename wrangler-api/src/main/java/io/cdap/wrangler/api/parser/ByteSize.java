/*
 * Copyright © 2025 YOUR NAME or Zeotap Assignment Submission
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Represents a parsed byte size value for Wrangler parser.
 */
public class ByteSize implements Token {
  private final String value;
  private final long bytes;

  public ByteSize(String value) {
    this.value = normalize(value);
    this.bytes = parseToBytes(this.value);
  }

  private String normalize(String input) {
    return input.trim().replaceAll("\\s+", "").toUpperCase();
  }

  private long parseToBytes(String input) {
    try {
      if (input.endsWith("KB")) {
        return (long) (Double.parseDouble(input.replace("KB", "")) * 1024);
      } else if (input.endsWith("MB")) {
        return (long) (Double.parseDouble(input.replace("MB", "")) * 1024 * 1024);
      } else if (input.endsWith("GB")) {
        return (long) (Double.parseDouble(input.replace("GB", "")) * 1024 * 1024 * 1024);
      } else if (input.endsWith("TB")) {
        return (long) (Double.parseDouble(input.replace("TB", "")) * 1024L * 1024 * 1024 * 1024);
      } else if (input.endsWith("B")) {
        return (long) Double.parseDouble(input.replace("B", ""));
      } else {
        throw new IllegalArgumentException();
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid byte size format: '" + input +
        "'. Expected formats like 10KB, 1.5MB, 2GB.");
    }
  }

  public long getBytes() {
    return bytes;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(value);
  }

  @Override
  public String toString() {
    return String.format("%s (bytes: %d)", value, bytes);
  }
}
