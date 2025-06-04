/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.indexhealthmonitor.action;

import org.opensearch.action.admin.cluster.health.ClusterHealthRequest;
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.opensearch.common.Table;
import org.opensearch.indexhealthmonitor.service.IndexHealthService;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestResponse;
import org.opensearch.rest.action.RestResponseListener;
import org.opensearch.rest.action.cat.AbstractCatAction;
import org.opensearch.rest.action.cat.RestTable;
import org.opensearch.transport.client.node.NodeClient;

import java.util.List;

import static org.opensearch.rest.RestRequest.Method.GET;

/**
 * REST Action that returns current client DB state
 */
public class IndexHealthMonitorAction extends AbstractCatAction {

  private final IndexHealthService service = new IndexHealthService();

  /**
   * Empty initialization constructor
   */
  public IndexHealthMonitorAction() {
  }

  /**
   * Documents this REST action.
   *
   * @return Syntax documentation.
   */
  public static String documentation() {
    return """
        /nn2/metrics
        /nn2/metrics/clusterName
        /nn2/metrics/taskMaxWaitingTime
        /nn2/metrics/status
        /nn2/metrics/activeShards
        /nn2/metrics/relocatingShards
        /nn2/metrics/initializingShards
        /nn2/metrics/unassignedShards
        /nn2/metrics/delayedUnassignedShards
        /nn2/metrics/numberOfNodes
        /nn2/metrics/numberOfDataNodes
        /nn2/metrics/activePrimaryShards
        /nn2/metrics/activeShardsPercent
        """;
  }

  @Override
  protected void documentation(StringBuilder sb) {
    sb.append(documentation());
  }

  @Override
  public List<Route> routes() {
    return List.of(
        new Route(GET, "/nn2/metrics"),
        new Route(GET, "/nn2/metrics/clusterName"),
        new Route(GET, "/nn2/metrics/taskMaxWaitingTime"),
        new Route(GET, "/nn2/metrics/status"),
        new Route(GET, "/nn2/metrics/activeShards"),
        new Route(GET, "/nn2/metrics/relocatingShards"),
        new Route(GET, "/nn2/metrics/initializingShards"),
        new Route(GET, "/nn2/metrics/unassignedShards"),
        new Route(GET, "/nn2/metrics/delayedUnassignedShards"),
        new Route(GET, "/nn2/metrics/numberOfNodes"),
        new Route(GET, "/nn2/metrics/numberOfDataNodes"),
        new Route(GET, "/nn2/metrics/activePrimaryShards"),
        new Route(GET, "/nn2/metrics/activeShardsPercent")
    );
  }

  @Override
  protected RestChannelConsumer doCatRequest(RestRequest request, NodeClient client) {
    String metric;

    if (request.path().split("/").length == 4) metric = request.path().split("/")[3];
    else metric = null;

    ClusterHealthRequest healthRequest = new ClusterHealthRequest();
    return channel -> client.admin().cluster().health(healthRequest, new RestResponseListener<>(channel) {
      @Override
      public RestResponse buildResponse(ClusterHealthResponse response) throws Exception {
        return RestTable.buildResponse(service.getIndexHealthStatus(response, metric), channel);
      }
    });
  }

  @Override
  protected Table getTableWithHeader(RestRequest request) {
    return null;
  }

  @Override
  public String getName() {
    return "index_health_monitor_action";
  }
}