package DataModels.Review;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Scott on 8/29/15.
 */
public class Review {

    private int reviewID;
    private int businessID;
    private int userID;
    private int starRating;
    private String reviewText;
    private long date;
    private int votes = 0;

    /**---------------------------------
     * ----------- Constructors --------
     * ---------------------------------*/
//    public Review(Map reviewParamsMap) {
////        if (null != reviewParamsMap.get("token")) {
////            this.nickname = String.valueOf(((String[])reviewParamsMap.get("nickname"))[0]);
////        }
////
////        if (null != reviewParamsMap.get("email")) {
////            this.email = String.valueOf(((String[])reviewParamsMap.get("email"))[0]);
////        }
//    }

    /** Both business and userID are crucial to a review*/
    public Review(int businessID, int userID) {
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(0);
        this.reviewText = "";
    }

    public Review(int businessID, int userID, int starRating) {
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(starRating);
        this.reviewText = "";
    }

    public Review(int businessID, int userID, String reviewText) {
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(0);
        this.reviewText = reviewText;
    }

    public Review(int businessID, int userID, int starRating, String reviewText) {
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(starRating);
        this.reviewText = reviewText;
    }

    public Review(int businessID, int userID, int starRating, String reviewText, String date) throws ParseException{
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(starRating);
        this.reviewText = reviewText;
        setDateWithString(date);
    }

    /**---------------------------------
     * ----------- Getters -------------
     * ---------------------------------*/
    public int getStarRating() {
        return starRating;
    }

    public int getUserID() {
        return userID;
    }

    public int getBusinessID() {
        return businessID;
    }

    public String getReviewText() {
        return reviewText;
    }

    public long getDate() {
        return date;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    /**---------------------------------
     * ----------- Setters -------------
     * ---------------------------------*/
    public void setStarRating(int starRating) {
        if (starRating > 5)
            this.starRating = 5;
        else if (starRating < 0)
            this.starRating = 0;
        this.starRating = starRating;
    }

    public void setReviewText(String reviewText) {
        if(null != reviewText && !reviewText.isEmpty())
            this.reviewText = reviewText;
    }

    public void setDate(long date) {
        this.date = date;
    }

    /** The input String has to be in yyyy-MM-dd format*/
    public void setDateWithString(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate = null;
        tempDate = dateFormat.parse(date);
        this.date = tempDate.getTime();
    }

    /**---------------------------------
     * ----------- Custom Methods ------
     * ---------------------------------*/
    public String toStringWithUsername(String username) {
        String composedString = String.format("%d - %s: %s\n", this.starRating, username, this.reviewText);
        return composedString;
    }
}
