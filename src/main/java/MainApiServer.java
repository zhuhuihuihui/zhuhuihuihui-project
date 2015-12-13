import DataModels.Business.Business;
import DataModels.User.User;
import Utils.Database;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
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
            if (null == user.getEmail() || null == user.getPassword() || null == user.getDevice()) {
                jsonResponse.put("success", false);
                String missingField = null;
                if (null == user.getEmail()) {
                    missingField = "email";
                } else if (null == user.getPassword()) {
                    missingField = "password";
                } else if (null == user.getDevice()) {
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

                user = Database.getInstance().checkUserPasswordMatches(user);
                if (null != user) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("token", user.getToken());
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
            Map<String, String[]> paramsMap = request.queryMap().toMap();
            User user = new User(paramsMap);

            JSONObject jsonResponse = new JSONObject();
            /** 1. Check missing fields */
            if (null == user.getEmail() || null == user.getPassword() || null == user.getDevice()) {
                jsonResponse.put("success", false);
                String missingField = null;
                if (null == user.getEmail()) {
                    missingField = "email";
                } else if (null == user.getPassword()) {
                    missingField = "password";
                } else if (null == user.getDevice()) {
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
            jsonResponse.put("token", user.getToken());
            response.status(200);          // set status code to 200
            response.type("application/json");     // set content type to application/json
            return jsonResponse;
        });

        get("/business/get", (request, response) -> {
            Map<String, String[]> paramsMap = request.queryMap().toMap();

            int defaultLimit = 50;
            String city = null;
            if (null != paramsMap.get("limit")) {
                defaultLimit = Integer.valueOf(paramsMap.get("limit")[0]);
            }
            if (null != paramsMap.get("city")) {
                city = String.valueOf(paramsMap.get("city")[0]);
            }

            JSONArray jsonResponse = new JSONArray();
            ArrayList<Business> resultBusinesses = Database.getInstance().getBusinessWith(city, defaultLimit);
            for (Business business: resultBusinesses) {
                JSONObject businessJSON = new JSONObject();
                businessJSON.put("businessID", business.getBusinessID());
                businessJSON.put("businessName", business.getBusinessName());
                businessJSON.put("city", business.getCity());
                businessJSON.put("state", business.getState());
                businessJSON.put("address", business.getAddress());
                businessJSON.put("latitude", business.getLatitude());
                businessJSON.put("longitude", business.getLongitude());
                businessJSON.put("rating", business.getStarRating());
                businessJSON.put("categories", business.getCategories());
                businessJSON.put("avatar", business.getPhotoURL());
                jsonResponse.add(businessJSON);
            }
            return jsonResponse.toJSONString();
        });

//        post("/business/add", (request, response) -> {
//            Map<String, String[]> paramsMap = request.queryMap().toMap();
////            User user = new User(paramsMap);
//            String.valueOf(((String[])paramsMap.get("nickname"))[0]);
//
//            JSONObject jsonResponse = new JSONObject();
//            /** 1. Check missing fields */
//            if (null == paramsMap.get("token") || null == paramsMap.get("name") || null == paramsMap.get("city") || null == paramsMap.get("address")) {
//                jsonResponse.put("success", false);
//                String missingField = null;
//                if (null == paramsMap.get("token")) {
//                    missingField = "token";
//                } else if (null == paramsMap.get("name")) {
//                    missingField = "name";
//                } else if (null == paramsMap.get("city")) {
//                    missingField = "city";
//                } else if (null == paramsMap.get("address")) {
//                    missingField = "address";
//                }
//                jsonResponse.put("error", "Field " + missingField + " is required.");
//                return jsonResponse.toJSONString();
//            }
//
//            int ret = Database.getInstance().insertBusinessWith(String.valueOf(((String[]) paramsMap.get("name"))[0]), String.valueOf(((String[]) paramsMap.get("city"))[0]), String.valueOf(((String[]) paramsMap.get("address"))[0]), 22);
//
//
//
//            /** 2. Validation */
////            if (!EmailValidator.getInstance().isValid(user.getEmail())) {
////                jsonResponse.put("success", false);
////                jsonResponse.put("error", "Field email is not a validated email address.");
////                return jsonResponse.toJSONString();
////            }
////
////            if (user.getPassword().length() < 6) {
////                jsonResponse.put("success", false);
////                jsonResponse.put("error", "Field password must be at least 6 digits long.");
////                return jsonResponse.toJSONString();
////            }
//
//
//        });
    }
}
