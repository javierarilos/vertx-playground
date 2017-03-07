# vertx-playground
Playing around with vertx.io & Swagger

## Requirements:
* JDK 1.8
* Maven

## Package and run:
```bash
echo "preparing the package..."
mvn package
echo "running the server..."
java -jar target/vertx-playground-3.3.3-fat.jar
```

## Playing with the server

```bash
echo "GET all issues..."
curl -v http://127.0.0.1:6556/v1/issue
echo "POST a new issue..."
curl -v -X POST http://localhost:6556/v1/issue -d '{"user_id": "mariona", "issue_desc": "sed broke"}'
echo "GET a single issue..."
curl -v http://127.0.0.1:6556/v1/issue/mariona
```


