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

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    issues.put("john", "router KO");
    issues.put("lucy", "phone power button broken");

    Handler<RoutingContext> sayHiHandler = routingContext -> {
      HttpServerResponse resp = routingContext.response();
      resp.putHeader("Content-Type", "text/plain");
      resp.end("Hello from Vert.x-Web");
    };


    Handler<RoutingContext> getIssuesHandler = routingContext -> {
      routingContext.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end(new JsonObject(issues).encode());
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
          .end(new JsonObject(issues).encode());
    };

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route(HttpMethod.GET, "/issue").handler(getIssuesHandler);
    router.route(HttpMethod.POST, "/issue").handler(newIssueHandler);
    router.route().handler(sayHiHandler);


    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router::accept).listen(8080);

  }
}
