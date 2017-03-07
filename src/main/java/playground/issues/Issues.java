package playground.issues;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Issues {

  public static Map<String, JsonObject> issues = new HashMap<>();

  public static void putIssue(String userId, String issueDesc){
    issues.put(userId, new JsonObject().put("user_id", userId).put("issue_desc", issueDesc));
  }

  public static JsonObject getIssue(String userId) {
    return issues.getOrDefault(userId, null);
  }

  public static void storeIssue(JsonObject body) {
    String userId = body.getString("user_id");
    issues.put(userId, body);
  }

  public static JsonArray getAllIssues() {
    return new JsonArray(new ArrayList(issues.values()));
  }
}
