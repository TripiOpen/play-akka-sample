package utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class ResponseFactory {

    public static ObjectNode createResponse(Object response, boolean ok) {
        ObjectNode result = Json.newObject();
        result.put("isSuccessfull", ok);
        if (response instanceof String) {
            result.put("body", (String) response);
        } else {
            result.putPOJO("body", response);
        }
        return result;
    }

    public static ObjectNode createResponse(boolean ok) {
        ObjectNode result = Json.newObject();
        result.put("isSuccessfull", ok);
        result.put("body", "");
        return result;
    }
}
