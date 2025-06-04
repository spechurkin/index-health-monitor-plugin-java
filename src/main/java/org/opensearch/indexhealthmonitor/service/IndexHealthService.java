package org.opensearch.indexhealthmonitor.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.opensearch.common.Table;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Additional service for tables
 */
public class IndexHealthService {

  /**
   * Empty initialization constructor
   */
  public IndexHealthService() {
  }

  /**
   * Map to JSON converter
   *
   * @param map requested map to convert
   * @return json
   * @throws IOException Input/Output handler
   */
  private String mapToJson(Map<String, Object> map) throws IOException {
    StringWriter writer = new StringWriter();
    JsonFactory factory = new JsonFactory();
    JsonGenerator generator = factory.createGenerator(writer);

    generator.writeStartObject();

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (value instanceof Map[]) {
        generator.writeArrayFieldStart(key);
        for (Map<String, String> phone : (Map<String, String>[]) value) {
          generator.writeStartObject();
          for (Map.Entry<String, String> phoneEntry : phone.entrySet()) {
            generator.writeStringField(phoneEntry.getKey(), phoneEntry.getValue());
          }
          generator.writeEndObject();
        }
        generator.writeEndArray();
      }
      generator.writeStringField(key, value.toString());
    }

    generator.writeEndObject();
    generator.close();

    return writer.toString();
  }

  private String pairToJson(String key, Object value) throws IOException {
    StringWriter writer = new StringWriter();
    JsonFactory factory = new JsonFactory();
    JsonGenerator generator = factory.createGenerator(writer);

    generator.writeStartObject();
    generator.writeStringField(key, value.toString());
    generator.writeEndObject();
    generator.close();

    return writer.toString();
  }

  /**
   * Health status extraction method
   *
   * @param response cluster admin data
   * @param metric   requested metric. If null, sends full metric
   * @return data table
   * @throws IOException Input/Output handler
   */
  public Table getIndexHealthStatus(ClusterHealthResponse response, String metric) throws IOException {
    Map<String, Object> map = new HashMap<>();
    map.put("clusterName", response.getClusterName());
    map.put("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    map.put("taskMaxWaitingTime", response.getTaskMaxWaitingTime());
    map.put("status", response.getStatus().name());
    map.put("activeShards", response.getActiveShards());
    map.put("relocatingShards", response.getRelocatingShards());
    map.put("initializingShards", response.getInitializingShards());
    map.put("unassignedShards", response.getUnassignedShards());
    map.put("delayedUnassignedShards", response.getDelayedUnassignedShards());
    map.put("numberOfNodes", response.getNumberOfNodes());
    map.put("numberOfDataNodes", response.getNumberOfDataNodes());
    map.put("activePrimaryShards", response.getActivePrimaryShards());
    map.put("activeShardsPercent", response.getActiveShardsPercent());

    Table table = new Table();
    table.startHeaders();
    table.addCell("Index Health Status JSON");
    table.endHeaders();

    if (metric == null) {
      table.startRow();
      table.addCell(mapToJson(map));
      table.endRow();
    } else if (map.containsKey(metric)) {
      table.startRow();
      table.addCell(pairToJson(metric, map.get(metric)));
      table.endRow();
    }

    return table;
  }
}
