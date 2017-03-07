package playground.issues;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class PostIssue implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    JsonObject body = routingContext.getBodyAsJson();
    Issues.storeIssue(body);

    routingContext.response()
        .setStatusCode(201)
        .putHeader("Content-Type", "application/json")
        .end(body.encode());
  }
}
