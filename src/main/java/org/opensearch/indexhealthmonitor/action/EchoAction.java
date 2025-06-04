package org.opensearch.indexhealthmonitor.action;

import org.opensearch.common.Table;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.indexhealthmonitor.service.JsonConverterService;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.cat.AbstractCatAction;
import org.opensearch.transport.client.node.NodeClient;

import java.util.List;

import static org.opensearch.rest.RestRequest.Method.GET;

/**
 * Simple *echo* action handler
 */
public class EchoAction extends AbstractCatAction {

  private final JsonConverterService service = new JsonConverterService();

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
    String message = request.hasParam("message") ? request.param("message") : "no message provided";
    return channel -> channel.sendResponse(
        new BytesRestResponse(
            RestStatus.OK,
            "application/json",
            service.getMessageFormatted(message)
        )
    );
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
