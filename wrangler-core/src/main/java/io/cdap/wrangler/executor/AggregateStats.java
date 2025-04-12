/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

//  package io.cdap.wrangler.executor;

//  import io.cdap.wrangler.api.Arguments;
//  import io.cdap.wrangler.api.Directive;
//  import io.cdap.wrangler.api.DirectiveContext;
//  import io.cdap.wrangler.api.DirectiveExecutionException;
//  import io.cdap.wrangler.api.Row;
//  import io.cdap.wrangler.api.parser.ByteSize;
//  import io.cdap.wrangler.api.parser.TimeDuration;
 
//  import java.util.Collections;
//  import java.util.List;
 
//  /**
//   * AggregateStats directive: aggregates byte sizes and time durations from rows.
//   */
//  public class AggregateStats implements Directive {
//    private String sizeCol;
//    private String timeCol;
//    private String outputSizeCol;
//    private String outputTimeCol;
//    private String outputUnitSize = "MB";
//    private String outputUnitTime = "s";
//    private String aggregationType = "total";
 
//    private long totalSizeBytes = 0;
//    private long totalTimeMs = 0;
//    private int rowCount = 0;
 
//    private boolean emitted = false; // ensure result is only returned once
 
//    @Override
//    public void initialize(Arguments arguments) {
//      sizeCol = arguments.value("size-column").value().toString();
//      timeCol = arguments.value("time-column").value().toString();
//      outputSizeCol = arguments.value("output-size-column").value().toString();
//      outputTimeCol = arguments.value("output-time-column").value().toString();
 
//      if (arguments.contains("unit-size")) {
//        outputUnitSize = arguments.value("unit-size").value().toString().toUpperCase();
//      }
 
//      if (arguments.contains("unit-time")) {
//        outputUnitTime = arguments.value("unit-time").value().toString().toLowerCase();
//      }
 
//      if (arguments.contains("aggregation-type")) {
//        aggregationType = arguments.value("aggregation-type").value().toString().toLowerCase();
//      }
//    }
 
//    @Override
//    public List<Row> execute(Row row, DirectiveContext context) throws DirectiveExecutionException {
//      Object sizeVal = row.getValue(sizeCol);
//      Object timeVal = row.getValue(timeCol);
 
//      if (sizeVal != null) {
//        ByteSize size = new ByteSize(sizeVal.toString());
//        totalSizeBytes += size.getBytes();
//      }
 
//      if (timeVal != null) {
//        TimeDuration time = new TimeDuration(timeVal.toString());
//        totalTimeMs += time.getMilliseconds();
//      }
 
//      rowCount++;
 
//      // Return only once: the final result row after all input rows processed
//      if (!context.hasNext() && !emitted) {
//        emitted = true;
 
//        double finalSize = totalSizeBytes;
//        double finalTime = totalTimeMs;
 
//        if (aggregationType.equals("avg") && rowCount > 0) {
//          finalSize = finalSize / rowCount;
//          finalTime = finalTime / rowCount;
//        }
 
//        finalSize = convertSize(finalSize, outputUnitSize);
//        finalTime = convertTime(finalTime, outputUnitTime);
 
//        Row result = new Row();
//        result.add(outputSizeCol, finalSize);
//        result.add(outputTimeCol, finalTime);
 
//        return Collections.singletonList(result);
//      }
 
//      return Collections.emptyList(); // don't output during row processing
//    }
 
//    private double convertSize(double bytes, String unit) {
//      switch (unit) {
//        case "KB": return bytes / 1024.0;
//        case "MB": return bytes / (1024.0 * 1024);
//        case "GB": return bytes / (1024.0 * 1024 * 1024);
//        default: return bytes;
//      }
//    }
 
//    private double convertTime(double ms, String unit) {
//      switch (unit) {
//        case "s": return ms / 1000.0;
//        case "min": return ms / (60.0 * 1000);
//        case "h": return ms / (3600.0 * 1000);
//        default: return ms;
//      }
//    }
 
//    @Override
//    public void destroy() {
//      // No cleanup needed
//    }
 
//    @Override
//    public UsageDefinition define() {
//      return UsageDefinition.builder("aggregate-stats")
//        .define("size-column", TokenType.COLUMN_NAME)
//        .define("time-column", TokenType.COLUMN_NAME)
//        .define("output-size-column", TokenType.COLUMN_NAME)
//        .define("output-time-column", TokenType.COLUMN_NAME)
//        .defineOptional("unit-size", TokenType.TEXT)
//        .defineOptional("unit-time", TokenType.TEXT)
//        .defineOptional("aggregation-type", TokenType.TEXT)
//        .build();
//    }
//  }

package io.cdap.wrangler.executor;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveContext;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.UsageDefinition.Builder;



import java.util.Collections;
import java.util.List;
/**
   * AggregateStats directive: aggregates byte sizes and time durations from rows.
   */
public class AggregateStats implements Directive {

  private String sizeCol;
  private String timeCol;
  private String outputSizeCol;
  private String outputTimeCol;
  private String outputUnitSize = "MB";
  private String outputUnitTime = "s";
  private String aggregationType = "total";

  private long totalSizeBytes = 0;
  private long totalTimeMs = 0;
  private int rowCount = 0;
  private boolean emitted = false;

  public void initialize(Arguments arguments) {
    sizeCol = arguments.value("size-column").value().toString();
    timeCol = arguments.value("time-column").value().toString();
    outputSizeCol = arguments.value("output-size-column").value().toString();
    outputTimeCol = arguments.value("output-time-column").value().toString();

    if (arguments.contains("unit-size")) {
      outputUnitSize = arguments.value("unit-size").value().toString().toUpperCase();
    }

    if (arguments.contains("unit-time")) {
      outputUnitTime = arguments.value("unit-time").value().toString().toLowerCase();
    }

    if (arguments.contains("aggregation-type")) {
      aggregationType = arguments.value("aggregation-type").value().toString().toLowerCase();
    }
  }

  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    for (Row row : rows) {
      Object sizeVal = row.getValue(sizeCol);
      Object timeVal = row.getValue(timeCol);

      if (sizeVal != null) {
        ByteSize size = new ByteSize(sizeVal.toString());
        totalSizeBytes += size.getBytes();
      }

      if (timeVal != null) {
        TimeDuration time = new TimeDuration(timeVal.toString());
        totalTimeMs += time.getMilliseconds();
      }

      rowCount++;
    }

    if (!emitted) {
      emitted = true;

      double finalSize = totalSizeBytes;
      double finalTime = totalTimeMs;

      if ("avg".equals(aggregationType) && rowCount > 0) {
        finalSize /= rowCount;
        finalTime /= rowCount;
      }

      finalSize = convertSize(finalSize);
      finalTime = convertTime(finalTime);

      Row result = new Row();
      result.add(outputSizeCol, finalSize);
      result.add(outputTimeCol, finalTime);
      return Collections.singletonList(result);
    }

    return Collections.emptyList();
  }

  private double convertSize(double bytes) {
    switch (outputUnitSize) {
      case "KB": return bytes / 1024.0;
      case "MB": return bytes / (1024.0 * 1024);
      case "GB": return bytes / (1024.0 * 1024 * 1024);
      default: return bytes;
    }
  }

  private double convertTime(double ms) {
    switch (outputUnitTime) {
      case "s": return ms / 1000.0;
      case "min": return ms / (60.0 * 1000);
      case "h": return ms / (3600.0 * 1000);
      default: return ms;
    }
  }

  @Override
  public UsageDefinition define() {
    Builder builder = UsageDefinition.builder("aggregate-stats");
    builder.define("size-column", TokenType.COLUMN_NAME);
    builder.define("time-column", TokenType.COLUMN_NAME);
    builder.define("output-size-column", TokenType.COLUMN_NAME);
    builder.define("output-time-column", TokenType.COLUMN_NAME);
    builder.define("unit-size", TokenType.TEXT);
    builder.define("unit-time", TokenType.TEXT);
    builder.define("aggregation-type", TokenType.TEXT);
    return builder.build();
  }
  
  public void destroy() {
    // Nothing to clean up
  }
  
}
