import DataModels.Business.Business;
import DataModels.Review.Review;
import DataModels.User.User;
import Utils.Database;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by Scott on 10/30/15.
 */
public class MainApiServer {
    private static final HashMap<String, String> corsHeaders = new HashMap<String, String>();

    static {
        corsHeaders.put("Access-Control-Allow-Methods", "HEAD, GET, POST, PUT, DELETE, OPTIONS, TRACE");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin, token");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    public final static void apply() {
        Filter filter = new Filter() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                corsHeaders.forEach((key, value) -> {
                    response.header(key, value);
                    System.out.println("Header set!!!");
                });
            }
        };
        Spark.after(filter);
    }

    public static void main(String[] args) {
        port(Integer.valueOf(System.getenv("PORT") == null ? "8090": System.getenv("PORT")));
        staticFileLocation("/public");

//        enableCORS("*", "HEAD, GET, POST, PUT, DELETE, OPTIONS, TRACE", "Content-Type, token");
        MainApiServer.apply();

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
            for (Business business : resultBusinesses) {
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

        post("/review/post", ((request, response) -> {
            Map<String, String[]> paramsMap = request.queryMap().toMap();
            JSONObject jsonResponse = new JSONObject();
            System.out.println(request);
            /** 1. Check missing fields */
            if (null == paramsMap.get("post") || null == paramsMap.get("businessID")) {
                jsonResponse.put("success", false);
                String missingField = null;
                if (null == paramsMap.get("post") || String.valueOf(paramsMap.get("post")[0]).isEmpty()) {
                    missingField = "key";
                } else if (null == paramsMap.get("businessID") || String.valueOf(paramsMap.get("businessID")[0]).isEmpty()) {
                    missingField = "businessID";
                }
                jsonResponse.put("error", "Field " + missingField + " is required.");
                return jsonResponse.toJSONString();
            }

            User user = Database.getInstance().getUserWithToken(String.valueOf(paramsMap.get("key")[0]));
            if (user.getUserID() != -1) {
                Database.getInstance().insertReviewWith(Integer.valueOf(paramsMap.get("businessID")[0]), user.getUserID(), Integer.valueOf(paramsMap.get("starRating")[0]), String.valueOf(paramsMap.get("reviewText")[0]));
            }
            jsonResponse.put("success", true);
            return jsonResponse.toJSONString();
        }));

        post("/review/get", ((request, response) -> {
            Map<String, String[]> paramsMap = request.queryMap().toMap();
            int defaultLimit = 10;
            int businessID = -1;
            String userEmail = null;
            int userID = -1;
            String sortBy = null;

            if (null != paramsMap.get("limit") && Integer.valueOf(paramsMap.get("limit")[0]) > 0) {
                defaultLimit = Integer.valueOf(paramsMap.get("limit")[0]);
            }

            if (null != paramsMap.get("businessID") && Integer.valueOf(paramsMap.get("businessID")[0]) > 0) {
                businessID = Integer.valueOf(paramsMap.get("businessID")[0]);
            }

            if (null != paramsMap.get("userID") && Integer.valueOf(paramsMap.get("userID")[0]) > 0) {
                businessID = Integer.valueOf(paramsMap.get("userID")[0]);
            }

            if (null != paramsMap.get("userEmail") && !String.valueOf(paramsMap.get("userEmail")[0]).isEmpty()) {
                userEmail = String.valueOf(paramsMap.get("userEmail")[0]);
            }

            if (null != paramsMap.get("sortBy") && !String.valueOf(paramsMap.get("sortBy")[0]).isEmpty()) {
                sortBy = String.valueOf(paramsMap.get("sortBy")[0]);
            }

            JSONArray jsonResponse = new JSONArray();
            ArrayList<Review> resultReviews = Database.getInstance().getReviewWith(defaultLimit, businessID, userEmail, userID, sortBy);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Review review : resultReviews) {
                JSONObject reviewJSON = new JSONObject();
                reviewJSON.put("userEmail", review.getUser().getEmail());
                reviewJSON.put("userNickname", review.getUser().getNickname());
                reviewJSON.put("starRating", review.getStarRating());
                reviewJSON.put("reviewText", review.getReviewText());
                reviewJSON.put("reviewDate", dateFormat.format(review.getDate()));
                reviewJSON.put("reviewVote", review.getVotes());
                jsonResponse.add(reviewJSON);
            }
            return jsonResponse.toJSONString();
        }));

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
