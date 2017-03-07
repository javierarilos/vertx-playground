package playground.issues;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class GetAllIssues implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    JsonArray allIssuesJson = Issues.getAllIssues();

    routingContext.response()
        .setStatusCode(200)
        .putHeader("Content-Type", "application/json")
        .end(allIssuesJson.encode());
  }
}
