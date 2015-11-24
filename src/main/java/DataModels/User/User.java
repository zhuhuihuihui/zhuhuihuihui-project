package DataModels.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Scott on 10/31/15.
 */

public class User {
    private int userID = -1;
    private String nickname = null;
    private String email = null;
    private String password = null;
    private Date birthday = null;
    private Gender gender = null;
    private String fromCity = null;
    private String university = null;
    private URL avatorUrl = null;
    private String token = null;
    private boolean isEmailVarified = false;

    public User(Map userParamsMap) {
        if (null != userParamsMap.get("nickname")) {
            this.nickname = String.valueOf(((String[])userParamsMap.get("nickname"))[0]);
        }

        if (null != userParamsMap.get("email")) {
            this.email = String.valueOf(((String[])userParamsMap.get("email"))[0]);
        }

        if (null != userParamsMap.get("password")) {
            this.password = String.valueOf(((String[])userParamsMap.get("password"))[0]);
        }

        if (null != userParamsMap.get("birthday")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date birthday = dateFormat.parse(String.valueOf(((String[])userParamsMap.get("birthday"))[0]));
                this.birthday = birthday;
            } catch (ParseException e) {
                e.printStackTrace();
                this.birthday = null;
            }
        }

        if (null != userParamsMap.get("gender")) {
            this.gender = Gender.genderWithString(String.valueOf(((String[])userParamsMap.get("gender"))[0]));
        }

        if (null != userParamsMap.get("fromCity")) {
            this.fromCity = String.valueOf(((String[])userParamsMap.get("fromCity"))[0]);
        }

        if (null != userParamsMap.get("university")) {
            this.university = String.valueOf(((String[])userParamsMap.get("university"))[0]);
        }

        if (null != userParamsMap.get("avatorUrl")) {
            try {
                this.avatorUrl = new URL(String.valueOf(((String[])userParamsMap.get("avatorUrl"))[0]));
            } catch (MalformedURLException e) {
                this.avatorUrl = null;
            }
        }

        this.token = UUID.randomUUID().toString();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public String getFromCity() {
        return fromCity;
    }

    public String getUniversity() {
        return university;
    }

    public URL getAvatorUrl() {
        return avatorUrl;
    }

    public String getToken() {
        return token;
    }

    public boolean isEmailVarified() {
        return isEmailVarified;
    }
}
