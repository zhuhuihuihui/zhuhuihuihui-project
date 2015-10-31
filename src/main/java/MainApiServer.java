import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

/**
 * Created by Scott on 10/30/15.
 */
public class MainApiServer {
    public static void main(String[] args) {
        port(Integer.valueOf(System.getenv("PORT") == null ? "8080": System.getenv("PORT")));
        staticFileLocation("/public");

        get("/", (req, res) -> "Hello World from api");
    }
}
