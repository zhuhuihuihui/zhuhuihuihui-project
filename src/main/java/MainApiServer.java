import Utils.Database;

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

        post("/", (request, response) -> {

        });
    }
}
