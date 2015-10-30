package WebPageServer;

import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

/**
 * Created by Scott on 10/30/15.
 */
public class MainWebPageServer {
    public static void main(String[] args) {
        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/public");

        
    }
}
