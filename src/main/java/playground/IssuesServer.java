package playground;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import playground.issues.GetAllIssues;
import playground.issues.GetIssue;
import playground.issues.Issues;
import playground.issues.PostIssue;

public class IssuesServer {


  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();

    Issues.putIssue("john", "router KO");
    Issues.putIssue("lucy", "phone power button broken");

    Handler<RoutingContext> getAllIssuesHandler = new GetAllIssues();
    Handler<RoutingContext> getIssueHandler = new GetIssue();
    Handler<RoutingContext> newIssueHandler = new PostIssue();

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());  // mandatory to get the body in other handlers.
    router.route(HttpMethod.GET, "/issue").handler(getAllIssuesHandler);
    router.route(HttpMethod.GET, "/issue/:userId").handler(getIssueHandler);
    router.route(HttpMethod.POST, "/issue").handler(newIssueHandler);


    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router::accept).listen(8080);

  }

}
