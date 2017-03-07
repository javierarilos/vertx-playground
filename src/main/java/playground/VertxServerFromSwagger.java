package playground;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import playground.issues.Issues;

import java.util.*;
import java.util.stream.Collectors;

public class VertxServerFromSwagger {

  public static void main(String[] args) {

    Issues.putIssue("john", "router KO");
    Issues.putIssue("lucy", "phone power button broken");

    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);

    Swagger apiSpec = new SwaggerParser().read("./src/main/resources/issues-swagger.yaml");

    VertxServerFromSwagger.loadApiIntoRouter(apiSpec, router);

    HttpServer httpServer = vertx.createHttpServer();
    httpServer.requestHandler(router::accept).listen(6556);
  }

  private static String swaggerPathToVertx(String basePath, String pathStr) {
    return basePath + pathStr.replace('{', ':').replace("}", "");
  }

  private static void printRequestPathParams(String pathStr, HttpServerRequest request) {
    System.out.printf("---- print request path params: %s %n", pathStr);
    for (String param : getParameterNames(pathStr)) {
      System.out.printf("    + %s: %s", param, request.getParam(param));
    }
  }

  private static List<String> getParameterNames(String pathStr) {
    return Arrays.stream(pathStr.split("/"))
        .filter(p -> p.contains(":"))
        .map(p -> p.replace(":", ""))
        .collect(Collectors.toList());
  }

  private static void loadApiIntoRouter(Swagger apiSpec, Router router) {

    router.route().handler(BodyHandler.create());  // mandatory to get the body in other handlers.
    addApiPaths(apiSpec, router);


  }

  private static void addApiPaths(Swagger apiSpec, Router router) {
    String basePath = apiSpec.getBasePath();
    for (Map.Entry<String, Path> entry : apiSpec.getPaths().entrySet()) {
      String swaggerPathStr = entry.getKey();
      String vertxPath = swaggerPathToVertx(basePath, swaggerPathStr);

      Path swaggerPathObj = entry.getValue();
      for (Map.Entry<io.swagger.models.HttpMethod, Operation> operationEntry : swaggerPathObj.getOperationMap().entrySet()) {
        HttpMethod vertxHttpMethod = swaggerHttpMethodToVertx(operationEntry.getKey());

        String swaggerOperationId = operationEntry.getValue().getOperationId();
        Handler<RoutingContext> routeHandler = resolveRouterHandler(swaggerOperationId);

        router.route(vertxHttpMethod, vertxPath).handler(routeHandler);
      }
    }
  }

  private static HttpMethod swaggerHttpMethodToVertx(io.swagger.models.HttpMethod swaggerHttpMethod) {
    switch (swaggerHttpMethod){
      case GET:
        return HttpMethod.GET;
      case POST:
        return HttpMethod.POST;
      default:
        throw new RuntimeException("UNSUPPORTED METHOD... REVIEW THE CODE.");
    }
  }

  private static Handler<RoutingContext> resolveRouterHandler(String operationId) {
    try {
      Class<Handler<RoutingContext>> x = (Class<Handler<RoutingContext>>) Class.forName(operationId);
      return x.newInstance();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();

    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
    return null;
  }

}
