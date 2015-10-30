package WebPageServer;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

/**
 * Created by Scott on 10/30/15.
 */
public class MainWebPageServer {
    public static void main(String[] args) {
//        port(Integer.valueOf(System.getenv("PORT")));
        port(8080);
        staticFileLocation("/public");

        get("/", (req, res) -> "Hello World");
    }
}
