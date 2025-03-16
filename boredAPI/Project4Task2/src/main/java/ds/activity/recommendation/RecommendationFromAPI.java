// Name: Hsiu-Yuan Yang
// AndrewID: hsiuyuay
// Email: hsiuyuay@andrew.cmu.edu
package ds.activity.recommendation;

/***
 * RecommendationFromAPI --- for extracting the needed information from the api
 * It has all attributes passed by the api response (i.e. activity name, activity type, number of participants, predicted price,
 * reference link, activity key in the api, accessibility indicator).
 */
public class RecommendationFromAPI {
    // to store the activity name
    String activity;
    // to store the activity type
    String type;
    // to store number of participants
    Integer participants;
    // to store predicted activity price
    Double price;
    // api may return a reference link back (depending on the activity)
    String link;
    // a key stored in the api, not used in this system
    String key;
    // an indicator stored in the api, not used in this system
    Double accessibility;

    // constructor
    public RecommendationFromAPI(String activity, String type, Integer participants, Double price, String link, String key, Double accessibility) {
        this.activity = activity;
        this.type = type;
        this.participants = participants;
        this.price = price;
        this.link = link;
        this.key = key;
        this.accessibility = accessibility;
    }

    // simple getter for activity
    public String getActivity() {
        return activity;
    }

    // simple setter for activity
    public void setActivity(String activity) {
        this.activity = activity;
    }

    // simple getter for type
    public String getType() {
        return type;
    }

    // simple setter for type
    public void setType(String type) {
        this.type = type;
    }

    // simple getter for participants
    public Integer getParticipants() {
        return participants;
    }

    // simple setter for participants
    public void setParticipants(int participants) {
        this.participants = participants;
    }

    // simple getter for price
    public Double getPrice() {
        return price;
    }

    // simple setter for price
    public void setPrice(double price) {
        this.price = price;
    }

    // simple getter for link
    public String getLink() {
        return link;
    }

    // simple setter for link
    public void setLink(String link) {
        this.link = link;
    }

    // simple getter for key
    public String getKey() {
        return key;
    }

    // simple setter for key
    public void setKey(String key) {
        this.key = key;
    }

    // simple getter for accessibility
    public Double getAccessibility() {
        return accessibility;
    }

    // simple setter for accessibility
    public void setAccessibility(double accessibility) {
        this.accessibility = accessibility;
    }
}
