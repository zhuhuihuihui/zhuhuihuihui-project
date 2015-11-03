package DataModels.User;

import java.net.URL;
import java.util.Date;

/**
 * Created by Scott on 10/31/15.
 */

public class User {
    private String nickname;
    private String email;
    private String password;
    private Date birthday;
    private Gender gender;
    private String fromCity;
    private String university;
    private URL avatorUrl;
    private String token;


    public static boolean login(String email, String password) {

        return false;
    }
}
