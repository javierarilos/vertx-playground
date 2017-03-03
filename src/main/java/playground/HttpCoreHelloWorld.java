package playground;
import io.vertx.core.Vertx;

import java.lang.management.ManagementFactory;

public class HttpCoreHelloWorld {

  public static void main(String[] args) {
    // Create an HTTP server which simply returns "Hello World!" to each request.
    Vertx.vertx().createHttpServer().requestHandler(req -> {
      String msg = String.format("Hello World! 8080 SALUTES YOU from %s req: %n", ManagementFactory.getRuntimeMXBean().getName(), req);
      System.out.println("*** println => "+msg);
      req.response().end(msg);
    }).listen(8080);
  }

}
