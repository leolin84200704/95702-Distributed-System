// Name: Hsiu-Yuan Yang
// AndrewID: hsiuyuay
// Email: hsiuyuay@andrew.cmu.edu
package ds.activity.recommendation;

public class Recommendation {
    // to store the username
    String username;
    // to store the activity name
    String activity;
    // to store the activity type
    String type;
    // to store number of participants
    Integer participants;
    // to store predicted activity price
    Double price;
    // api may return a reference link back (depending on the activity)


    // constructor
    public Recommendation(String username, String activity, String type, Integer participants, Double price) {
        this.username = username;
        this.activity = activity;
        this.type = type;
        this.participants = participants;
        this.price = price;
    }

    // simple getter for username
    public String getUsername() {
        return username;
    }

    // simple setter for username
    public void setUsername(String username) {
        this.username = username;
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
    public int getParticipants() {
        return participants;
    }

    // simple setter for participants
    public void setParticipants(int participants) {
        this.participants = participants;
    }

    // simple getter for price
    public double getPrice() {
        return price;
    }

    // simple setter for price
    public void setPrice(double price) {
        this.price = price;
    }
}
