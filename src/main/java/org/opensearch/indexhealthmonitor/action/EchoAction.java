package org.opensearch.indexhealthmonitor.action;

import org.opensearch.action.admin.cluster.health.ClusterHealthRequest;
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.opensearch.common.Table;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestResponse;
import org.opensearch.rest.action.RestResponseListener;
import org.opensearch.rest.action.cat.AbstractCatAction;
import org.opensearch.rest.action.cat.RestTable;
import org.opensearch.transport.client.node.NodeClient;

import java.util.List;

import static org.opensearch.rest.RestRequest.Method.GET;

/**
 * Simple *echo* action handler
 */
public class EchoAction extends AbstractCatAction {

  /**
   * Empty initialization constructor
   */
  public EchoAction() {
  }

  /**
   * Documents this REST action.
   *
   * @return Syntax documentation.
   */
  public static String documentation() {
    return """
        /echo
        /echo/{message}
        """;
  }

  @Override
  protected RestChannelConsumer doCatRequest(RestRequest request, NodeClient client) {
    String message = request.hasParam("message") ? request.param("message") : null;
    ClusterHealthRequest healthRequest = new ClusterHealthRequest();
    return channel -> client.admin().cluster().health(healthRequest, new RestResponseListener<>(channel) {
      @Override
      public RestResponse buildResponse(ClusterHealthResponse response) throws Exception {
        if (message != null) {
          return RestTable.buildResponse(
              getTableWithHeader(request)
                  .startRow()
                  .addCell("Your message was:\n" + message)
                  .endRow(),
              channel);
        }
        return RestTable.buildResponse(
            getTableWithHeader(request)
                .startRow()
                .addCell("no message provided")
                .endRow(),
            channel);
      }
    });
  }

  @Override
  protected void documentation(StringBuilder sb) {
    sb.append(documentation());
  }

  @Override
  protected Table getTableWithHeader(RestRequest request) {
    return new Table().startHeaders().addCell("message").endHeaders();
  }

  @Override
  public List<Route> routes() {
    return List.of(
        new Route(GET, "/echo"),
        new Route(GET, "/echo/{message}")
    );
  }

  @Override
  public String getName() {
    return "echo_action";
  }
}
