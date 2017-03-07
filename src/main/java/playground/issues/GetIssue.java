package playground.issues;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class GetIssue  implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
      String userId = routingContext.request().getParam("user_id");
      JsonObject issueJson = Issues.getIssue(userId);

      if (issueJson == null) {
        routingContext.response()
            .setStatusCode(404)
            .putHeader("Content-Type", "application/json")
            .end();
      } else {
        routingContext.response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end(issueJson.encode());

      }
    };

}
