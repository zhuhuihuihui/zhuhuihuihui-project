import DataModels.User.User;
import Utils.Database;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by Scott on 10/30/15.
 */
public class MainApiServer {
    public static void main(String[] args) {
        port(Integer.valueOf(System.getenv("PORT") == null ? "8080": System.getenv("PORT")));
        staticFileLocation("/public");

        Database.getInstance().getUserWithEmail("");

        get("/", (req, res) -> "Hello World from api");

        post("/login", (request, response) -> {
            Map<String, String[]> paramsMap = request.queryMap().toMap();
            User user = new User(paramsMap);

            JSONObject jsonResponse = new JSONObject();
            /** 1. Check missing fields */
            if (null == user.getEmail() || null == user.getPassword() || !paramsMap.containsKey("device")) {
                jsonResponse.put("success", false);
                String missingField = null;
                if (null == user.getEmail()) {
                    missingField = "email";
                } else if (null == user.getPassword()) {
                    missingField = "password";
                } else if (!paramsMap.containsKey("device")) {
                    missingField = "device";
                }
                jsonResponse.put("error", "Field " + missingField + " is required.");
                return jsonResponse.toJSONString();
            }

            /** 2. Validation */
            if (!EmailValidator.getInstance().isValid(user.getEmail())) {
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Field email is not a validated email address.");
                return jsonResponse.toJSONString();
            }

            if (user.getPassword().length() < 6) {
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Field password must be at least 6 digits long.");
                return jsonResponse.toJSONString();
            }

            /** 3. Check if we have this user and if the password matches */
            try {
                if (!Database.getInstance().isUserExistInTheUserTable(user)) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("error", "We don't have this user.");
                    return jsonResponse.toJSONString();
                }

                if (Database.getInstance().checkUserPasswordMatches(user)) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("token", "A token should be here");
                    return jsonResponse.toJSONString();
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("error", "Password incorrect.");
                    return jsonResponse.toJSONString();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Backend database error, request is not being proceed.");
                return jsonResponse.toJSONString();
            }
        });

        post("/signup", (request, response) -> {
            System.out.println("Raw = " + request.raw());
            System.out.println("headers = " + request.headers());
            System.out.println("body = " + request.body());
            System.out.println("route pattern params = " + request.params());
            System.out.println("route pattern username = " + request.params("username"));
            System.out.println("query params = " + request.queryParams());
            System.out.println("query username = " + request.queryParams("username"));

            Map<String, String[]> paramsMap = request.queryMap().toMap();
            User user = new User(paramsMap);

            JSONObject jsonResponse = new JSONObject();
            /** 1. Check missing fields */
            if (null == user.getEmail() || null == user.getPassword() || !paramsMap.containsKey("device")) {
                jsonResponse.put("success", false);
                String missingField = null;
                if (null == user.getEmail()) {
                    missingField = "email";
                } else if (null == user.getPassword()) {
                    missingField = "password";
                } else if (!paramsMap.containsKey("device")) {
                    missingField = "device";
                }
                jsonResponse.put("error", "Field " + missingField + " is required.");
                return jsonResponse.toJSONString();
            }

            /** 2. Validation */
            if (!EmailValidator.getInstance().isValid(user.getEmail())) {
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Field email is not a validated email address.");
                return jsonResponse.toJSONString();
            }

            if (user.getPassword().length() < 6) {
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Field password must be at least 6 digits long.");
                return jsonResponse.toJSONString();
            }

            /** 3. Check if it's existing user */
            try {
                if (Database.getInstance().isUserExistInTheUserTable(user)) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("error", "Email already exist.");
                    return jsonResponse.toJSONString();
                }


                Database.getInstance().insertUserIntoUserTable(user);
            } catch (SQLException e) {
                e.printStackTrace();
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Backend database error, request is not being proceed.");
                return jsonResponse.toJSONString();
            }


            jsonResponse.put("success", true);
            jsonResponse.put("token", "a token should be here");
            response.status(200);          // set status code to 200
            response.type("application/json");     // set content type to application/json
            return jsonResponse;
        });
    }
}
