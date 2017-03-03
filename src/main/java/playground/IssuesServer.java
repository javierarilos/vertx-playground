package playground;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IssuesServer {
  public static Map<String, JsonObject> issues = new HashMap<>();

  public static void putIssue(String userId, String issueDesc){
    issues.put(userId, new JsonObject().put("user_id", userId).put("issue_desc", issueDesc));
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    putIssue("john", "router KO");
    putIssue("lucy", "phone power button broken");

    Handler<RoutingContext> getAllIssuesHandler = routingContext -> {
      JsonArray allIssuesJson = new JsonArray(new ArrayList(issues.values()));

      routingContext.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end(allIssuesJson.encode());
    };

    Handler<RoutingContext> getIssueHandler = routingContext -> {
      String userId = routingContext.request().getParam("userId");
      JsonObject issueJson = issues.getOrDefault(userId, null);

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

    Handler<RoutingContext> newIssueHandler = routingContext -> {
      /*  Body is in json in the form:
          {"user_id": "martha", "issue_desc": "some problem"}
      */
      JsonObject body = routingContext.getBodyAsJson();
      String userId = body.getString("user_id");
      issues.put(userId, body);

      routingContext.response()
          .setStatusCode(201)
          .putHeader("Content-Type", "application/json")
          .end(body.encode());
    };

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());  // mandatory to get the body in other handlers.
    router.route(HttpMethod.GET, "/issue").handler(getAllIssuesHandler);
    router.route(HttpMethod.GET, "/issue/:userId").handler(getIssueHandler);
    router.route(HttpMethod.POST, "/issue").handler(newIssueHandler);


    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router::accept).listen(8080);

  }
}
