/*
 * Copyright © 2025 YOUR NAME or Zeotap Assignment Submission
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

 package io.cdap.wrangler.api.parser;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonPrimitive;
 
 /**
  * Represents a parsed time duration value for Wrangler parser.
  */
 public class TimeDuration implements Token {
   private final String value;
   private final long milliseconds;
 
   public TimeDuration(String value) {
     this.value = normalize(value);
     this.milliseconds = parseToMilliseconds(this.value);
   }
 
   private String normalize(String input) {
     return input.trim().replaceAll("\\s+", "").toLowerCase();
   }
 
   private long parseToMilliseconds(String input) {
     try {
       if (input.endsWith("ms")) {
         return (long) Double.parseDouble(input.replace("ms", ""));
       } else if (input.endsWith("s")) {
         return (long) (Double.parseDouble(input.replace("s", "")) * 1000);
       } else if (input.endsWith("min")) {
         return (long) (Double.parseDouble(input.replace("min", "")) * 60 * 1000);
       } else if (input.endsWith("h")) {
         return (long) (Double.parseDouble(input.replace("h", "")) * 3600 * 1000);
       } else {
         throw new IllegalArgumentException();
       }
     } catch (Exception e) {
       throw new IllegalArgumentException("Invalid time duration format: '" + input +
         "'. Expected formats like 500ms, 2.5s, 3min, 1.5h.");
     }
   }
 
   public long getMilliseconds() {
     return milliseconds;
   }
 
   @Override
   public String value() {
     return value;
   }
 
   @Override
   public TokenType type() {
     return TokenType.TIME_DURATION;
   }
 
   @Override
   public JsonElement toJson() {
     return new JsonPrimitive(value);
   }
 
   @Override
   public String toString() {
     return String.format("%s (ms: %d)", value, milliseconds);
   }
 }
