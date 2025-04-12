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


 package io.cdap.wrangler.executor;

 import io.cdap.wrangler.TestingRig;
 import io.cdap.wrangler.api.Row;
 import org.junit.Test;
 import java.util.Arrays;
 import java.util.List;
 
 import static org.junit.Assert.assertEquals;

 public class AggregateStatsTest {
 
   @Test
public void testTotalAggregation() throws Exception {
  List<Row> rows = Arrays.asList(
    new Row("bytes", "1KB").add("duration", "2s"),
    new Row("bytes", "3KB").add("duration", "4s")
  );

  String[] recipe = {
    "aggregate-stats size-column=:bytes time-column=:duration " +
    "output-size-column=total_size output-time-column=total_time " +
    "unit-size=MB unit-time=s aggregation-type=total"
  };

  List<Row> results = TestingRig.execute(recipe, rows);
  Row result = results.get(0);

  assertEquals(0.00390625, (Double) result.getValue("total_size"), 0.0001);
  assertEquals(6.0, (Double) result.getValue("total_time"), 0.001);
}

@Test
public void testAverageAggregation() throws Exception {
  List<Row> rows = Arrays.asList(
    new Row("bytes", "2KB").add("duration", "2s"),
    new Row("bytes", "6KB").add("duration", "4s")
  );

  String[] recipe = {
    "aggregate-stats size-column=:bytes time-column=:duration " +
    "output-size-column=avg_size output-time-column=avg_time " +
    "unit-size=MB unit-time=s aggregation-type=avg"
  };

  List<Row> results = TestingRig.execute(recipe, rows);
  Row result = results.get(0);

  assertEquals(0.00390625, (Double) result.getValue("avg_size"), 0.0001);
  assertEquals(3.0, (Double) result.getValue("avg_time"), 0.001);
}

 }
