import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

/**
 * Created by Scott on 10/30/15.
 */
public class MainWebPageServer {
    public static void main(String[] args) {
        port(Integer.valueOf(System.getenv("PORT") == null ? "8080": System.getenv("PORT")));
        staticFileLocation("/public");

        get("/", (req, res) -> "Hello World");

        get("/signup", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Signup page!");

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());
    }
}
