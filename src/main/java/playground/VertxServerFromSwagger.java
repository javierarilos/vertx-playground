package playground;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import playground.issues.Issues;

import java.util.*;
import java.util.stream.Collectors;

public class VertxServerFromSwagger {

  public static final int PORT = 6556;
  public static final String API_SPEC_FPATH = "./src/main/resources/issues-swagger.yaml";

  public static void main(String[] args) {

    Issues.putIssue("john", "router KO");
    Issues.putIssue("lucy", "phone power button broken");

    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);

    Swagger apiSpec = new SwaggerParser().read(API_SPEC_FPATH);

    Swagger2Vertex.createSwaggerApiInVertexRouter(apiSpec, router);

    HttpServer httpServer = vertx.createHttpServer();
    httpServer.requestHandler(router::accept).listen(PORT);
    System.out.printf(">>>> VertxServerFromSwagger API on port=%d spec from: %s", PORT, API_SPEC_FPATH);
  }

}
