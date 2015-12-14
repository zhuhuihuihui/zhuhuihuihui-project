package DataModels.Review;

import DataModels.Business.Business;
import DataModels.User.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Scott on 8/29/15.
 */
public class Review {

    private int reviewID;
    private Business business;
    private User user;
    private int starRating;
    private String reviewText;
    private Date date;
    private int votes = 0;

    /**---------------------------------
     * ----------- Constructors --------
     * ---------------------------------*/
    public Review() {

    }
    /** Both business and userID are crucial to a review*/

    /**---------------------------------
     * ----------- Getters -------------
     * ---------------------------------*/
    public int getStarRating() {
        return starRating;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Date getDate() {
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

    public void setDate(Date date) {
        this.date = date;
    }

    /** The input String has to be in yyyy-MM-dd format*/
//    public void setDateWithString(String date) throws ParseException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date tempDate = null;
//        tempDate = dateFormat.parse(date);
//        this.date = tempDate.getTime();
//    }

    /**---------------------------------
     * ----------- Custom Methods ------
     * ---------------------------------*/
    public String toStringWithUsername(String username) {
        String composedString = String.format("%d - %s: %s\n", this.starRating, username, this.reviewText);
        return composedString;
    }
}
