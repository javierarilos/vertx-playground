package playground;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;

public class IssuesServer {
public static Map<String, Object> issues = new HashMap<>();

  public static JsonObject getIssueJson(String userId) {
    Object issueDesc = issues.getOrDefault(userId, "");
    return new JsonObject()
                .put("user_id", userId)
                .put("issue_desk", issueDesc);
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    issues.put("john", "router KO");
    issues.put("lucy", "phone power button broken");

    Handler<RoutingContext> sayHiHandler = routingContext -> {
      HttpServerResponse resp = routingContext.response();
      resp.putHeader("Content-Type", "text/plain");
      resp.end("Hello from Vert.x-Web");
    };


    Handler<RoutingContext> getAllIssuesHandler = routingContext -> {
      routingContext.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end(new JsonObject(issues).encode());
    };

    Handler<RoutingContext> getIssueHandler = routingContext -> {
      String userId = routingContext.request().getParam("userId");
      routingContext.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end(getIssueJson(userId).encode());
    };

    Handler<RoutingContext> newIssueHandler  = routingContext -> {
      /*  Body is in json in the form:
          {"user_id": "martha", "issue_desc": "some problem"}
      */
      JsonObject body = routingContext.getBodyAsJson();
      String userId = body.getString("user_id");
      String issueDesc = body.getString("issue_desc");
      issues.put(userId, issueDesc);

      routingContext.response()
          .setStatusCode(201)
          .putHeader("Content-Type", "application/json")
          .end(getIssueJson(userId).encode());
    };

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());  // mandatory to get the body in other handlers.
    router.route(HttpMethod.GET, "/issue").handler(getAllIssuesHandler);
    router.route(HttpMethod.GET, "/issue/:userId").handler(getIssueHandler);
    router.route(HttpMethod.POST, "/issue").handler(newIssueHandler);
    router.route().handler(sayHiHandler);


    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router::accept).listen(8080);

  }
}
