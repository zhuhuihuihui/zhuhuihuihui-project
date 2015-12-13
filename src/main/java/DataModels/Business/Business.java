package DataModels.Business;

import org.json.simple.JSONArray;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Scott on 8/29/15.
 */
public class Business implements Comparable<Business>{

    private int businessID;
    private String businessName;
    private ArrayList<String> businessNeighborhoods;
    private String address;
    private String city;
    private String state;
    private double latitude;
    private double longitude;
    private float starRating;
    private long reviewCount;
    private String photoURL;
    private String categories;
    private boolean isBusinessStillOpen;
    private ArrayList<String> schools;
    private URL yelpURL;

    /**---------------------------------
     * ----------- Constructors --------
     * ---------------------------------*/

    public Business(int businessID) {
        this.businessID = businessID;
        this.businessName = "";
        this.businessNeighborhoods = new ArrayList<String>();
        this.address = "";
        this.city = "";
        this.state = "";
        this.latitude = 0;
        this.longitude = 0;
        this.starRating = 0;
        this.reviewCount = 0;
        this.categories = "";
        this.isBusinessStillOpen = false;
        this.schools = new ArrayList<String>();
    }

    public Business(int businessId, String name, String city, String state, double latitude, double longitude) {
        this(businessId);
        this.businessName = name;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**---------------------------------
     * ----------- Getters -------------
     * ---------------------------------*/
    public ArrayList<String> getBusinessNeighborhoods() {
        return businessNeighborhoods;
    }

    public String getBusinessNeighborhoodsAsCommaSeparatedString() {
        return String.join(", ", businessNeighborhoods);
    }

    public String getBusinessName() {
        return businessName;
    }

    public int getBusinessID() {
        return businessID;
    }

    /**---------------------------------
     * ----------- Setters -------------
     * ---------------------------------*/
    public void setBusinessNeighborhoods(ArrayList<String> businessNeighborhoods) {
        this.businessNeighborhoods = businessNeighborhoods;
        this.sortNeighborhoodsAlphabetically();
    }

    public void setBusinessNeighborhoodsWithJSONArray(JSONArray neighborhoods) {
        for (int i = 0; i < neighborhoods.size(); i++) {
            String neighborhood = (String)neighborhoods.get(i);
            this.businessNeighborhoods.add(neighborhood.trim());
        }
        this.sortNeighborhoodsAlphabetically();
    }

    public void setBusinessNeighborhoodsWithCommaSeparatedString(String neighborhoods) {
        /** This line of code's idea comes from this post:
         * http://stackoverflow.com/questions/7488643/java-how-to-convert-comma-separated-string-to-arraylist
         * */
        this.businessNeighborhoods = new ArrayList<String>(Arrays.asList(neighborhoods.trim().split(",")));
        ArrayList<String> tempArray = new ArrayList<String>();
        for (String neighborhood :this.businessNeighborhoods) {
            tempArray.add(neighborhood.trim());
        }
        this.businessNeighborhoods = tempArray;
        this.sortNeighborhoodsAlphabetically();
    }

    /**---------------------------------
     * ----------- Custom Methods ------
     * ---------------------------------*/
    private void sortNeighborhoodsAlphabetically() {
        Collections.sort(this.businessNeighborhoods, new Comparator<String>() {
            @Override
            public int compare(String string1, String string2) {
                return string1.compareToIgnoreCase(string2);
            }
        });
    }

    @Override
    public String toString() {
        String composedString =
                String.format("%s - %s, %s (%s, %s) (%s)\n",
                this.businessName,
                this.city,
                this.state,
                this.latitude,
                this.longitude,
                this.getBusinessNeighborhoodsAsCommaSeparatedString());
        return composedString;
    }

    @Override
    public int compareTo(Business business) {
        int isBusinessNameEqual = this.getBusinessName().compareTo(business.getBusinessName());
        return 0 == isBusinessNameEqual? (this.getBusinessID() == business.getBusinessID()? 0: 1): isBusinessNameEqual;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public void setBusinessID(int businessID) {
        this.businessID = businessID;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getStarRating() {
        return this.starRating;
    }

    public void setStarRating(float starRating) {
        this.starRating = starRating;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
