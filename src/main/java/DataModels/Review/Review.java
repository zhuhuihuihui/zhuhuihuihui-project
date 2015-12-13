package DataModels.Review;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Scott on 8/29/15.
 */
public class Review implements Comparable<Review>{

    private String businessID;
    private String userID;
    private int starRating;
    private String reviewText;
    private long date;
    private int[] votes = new int[] {0, 0, 0};

    /**---------------------------------
     * ----------- Constructors --------
     * ---------------------------------*/

    /** Both business and userID are crucial to a review*/
    public Review(String businessID, String userID) {
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(0);
        this.reviewText = "";
    }

    public Review(String businessID, String userID, int starRating) {
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(starRating);
        this.reviewText = "";
    }

    public Review(String businessID, String userID, String reviewText) {
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(0);
        this.reviewText = reviewText;
    }

    public Review(String businessID, String userID, int starRating, String reviewText) {
        this.businessID = businessID;
        this.userID = userID;
        setStarRating(starRating);
        this.reviewText = reviewText;
    }

    public Review(String businessID, String userID, int starRating, String reviewText, String date) throws ParseException{
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

    public int getVote(VOTE_TYPE type) {
        return votes[type.ordinal()];
    }

    public String getUserID() {
        return userID;
    }

    public String getBusinessID() {
        return businessID;
    }

    public String getReviewText() {
        return reviewText;
    }

    public long getDate() {
        return date;
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

    public void addVote(VOTE_TYPE type) {
        votes[type.ordinal()]++;
    }

    /**---------------------------------
     * ----------- Custom Methods ------
     * ---------------------------------*/
    public String toStringWithUsername(String username) {
        String composedString = String.format("%d - %s: %s\n", this.starRating, username, this.reviewText);
        return composedString;
    }

    @Override
    public int compareTo(Review review) {
        long isReviewTimeEqual = this.getDate() - review.getDate();
        return 0 == isReviewTimeEqual? this.getUserID().compareTo(review.getUserID()): ((isReviewTimeEqual > 0)? 1: -1);
    }
}
