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

package org.opensearch.indexhealthmonitor;

import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.IndexScopedSettings;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.SettingsFilter;
import org.opensearch.indexhealthmonitor.action.EchoAction;
import org.opensearch.indexhealthmonitor.action.IndexHealthMonitorAction;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;

import java.util.List;
import java.util.function.Supplier;

/**
 * REST plugin showing full DB health
 */
public class IndexHealthMonitorPlugin extends Plugin implements ActionPlugin {

  /**
   * Instantiate this plugin.
   */
  public IndexHealthMonitorPlugin() {
  }

  @Override
  public List<RestHandler> getRestHandlers(
      Settings settings,
      RestController restController,
      ClusterSettings clusterSettings,
      IndexScopedSettings indexScopedSettings,
      SettingsFilter settingsFilter,
      IndexNameExpressionResolver indexNameExpressionResolver,
      Supplier<DiscoveryNodes> nodesInCluster
  ) {
    return List.of(new IndexHealthMonitorAction(), new EchoAction());
  }
}