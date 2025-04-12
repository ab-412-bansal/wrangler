/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package io.cdap.wrangler.utils;

 import io.cdap.wrangler.api.parser.TimeDuration;
 import org.junit.Test;
 import static org.junit.Assert.assertEquals;
 
 public class TimeDurationTest {
   @Test
   public void testValidParsing() {
     assertEquals(500, new TimeDuration("500ms").getMilliseconds());
     assertEquals(2000, new TimeDuration("2s").getMilliseconds());
     assertEquals(180000, new TimeDuration("3min").getMilliseconds());
     assertEquals(5400000, new TimeDuration("1.5h").getMilliseconds());
   }
 
   @Test(expected = IllegalArgumentException.class)
   public void testInvalidFormat() {
     new TimeDuration("3zz");
   }
 
   @Test
   public void testCaseInsensitive() {
     assertEquals(3000, new TimeDuration("3S").getMilliseconds());
   }
 }
