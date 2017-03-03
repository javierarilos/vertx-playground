package playground;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

public class VertxCorePlayground {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    Handler<HttpServerRequest> helloWorldResponse = req -> req.response()
                                                                  .putHeader("Content-Type", "text/plain")
                                                                  .end("Hello World!");

    System.out.println("... starting webserver in localhost:8080");
    HttpServer server = vertx.createHttpServer()
        .requestHandler(helloWorldResponse)
        .listen(8087);

    System.out.println("... Timer example");
    vertx.setTimer(3000, event -> System.out.println("    <**> Timer fired <**>"));

    vertx.setTimer(5000, event -> {
      System.out.println("... Stopping the webserver");
      server.close();
      vertx.close();
    });

    System.out.println("... execute a blocking call");
    vertx.executeBlocking(
        future -> {
          System.out.println("    -| sleeping 500 |-");
          try {
            Thread.sleep(500);
            future.complete("OK");
          } catch (InterruptedException e) {
            e.printStackTrace();
            future.complete("KO");
          }
        },
        res -> {
          System.out.printf("    -| blocking call finished with result=%s |-%n", res.result());
        }
    );
  }
}
