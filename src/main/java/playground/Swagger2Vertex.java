package playground;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Map;

public class Swagger2Vertex {
  public static String convertPath(String basePath, String swaggerPath) {
    // eg: basePath=/v1 swaggerPath=/issue/{issue_id} => /v1/issue/:issue_id
    return basePath + swaggerPath.replace('{', ':').replace("}", "");
  }

  public static void createSwaggerApiInVertexRouter(Swagger apiSpec, Router router) {
    router.route().handler(BodyHandler.create());  // mandatory to get the body in other handlers.
    addApiPaths(apiSpec, router);
  }

  public static void addApiPaths(Swagger apiSpec, Router router) {
    // API -> N*Paths Path -> Operations
    String basePath = apiSpec.getBasePath();
    for (Map.Entry<String, Path> entry : apiSpec.getPaths().entrySet()) {
      String swaggerPathStr = entry.getKey();
      String vertxPath = convertPath(basePath, swaggerPathStr);
      Path swaggerPathObj = entry.getValue();

      addApiOperations(router, vertxPath, swaggerPathObj);
    }
  }

  static void addApiOperations(Router router, String vertxPath, Path swaggerPathObj) {
    for (Map.Entry<io.swagger.models.HttpMethod, Operation> operationEntry : swaggerPathObj.getOperationMap().entrySet()) {
      HttpMethod vertxHttpMethod = swaggerHttpMethodToVertx(operationEntry.getKey());

      String swaggerOperationId = operationEntry.getValue().getOperationId();
      Handler<RoutingContext> routeHandler = resolveRouterHandler(swaggerOperationId);

      router.route(vertxHttpMethod, vertxPath).handler(routeHandler);
    }
  }

  static HttpMethod swaggerHttpMethodToVertx(io.swagger.models.HttpMethod swaggerHttpMethod) {
    switch (swaggerHttpMethod){
      case GET:
        return HttpMethod.GET;
      case POST:
        return HttpMethod.POST;
      default:
        throw new RuntimeException("UNSUPPORTED METHOD... REVIEW THE CODE.");
    }
  }

  static Handler<RoutingContext> resolveRouterHandler(String operationId) {

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
