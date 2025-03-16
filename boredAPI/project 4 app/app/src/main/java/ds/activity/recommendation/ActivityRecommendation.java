// Name: Hsiu-Yuan Yang
// AndrewID: hsiuyuay
// Email: hsiuyuay@andrew.cmu.edu
package ds.activity.recommendation;

// load stuff we need
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

/***
 * class ActivityRecommendation --- the main class which records user input and then calls get activity to connect to BoredAPI for activity recommendation.
 * After creating the search term, it then calls get activity background thread to do the http doGet and the background thread will then pass the response back
 * to this UI thread for displaying the results.
 */
public class ActivityRecommendation extends AppCompatActivity {
    // to store user choice
    private static String userSelection;
    ActivityRecommendation me = this;

    /***
     * onCreate --- initiate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ActivityRecommendation ar = this;

        // find the widgets needed
        Button submitButton = (Button)findViewById(R.id.submit);
        RadioGroup selectionGroup = (RadioGroup) findViewById(R.id.options);
        Spinner typeSpinner = (Spinner) findViewById(R.id.activityType);
        Slider participantSlider = (Slider) findViewById(R.id.numParticipants);

        // The radio group setOnCheckedChangeListener will be triggered when the user clicks on different radio buttons
        selectionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            /***
             * onCheckedChanged --- when user selects a radio button, get their option and display relevant widgets / hide unused widgets
             * @param selectionGroup the radio group
             * @param checkedId the selected radio button's id
             */
            @Override
            public void onCheckedChanged(RadioGroup selectionGroup, int checkedId) {
                // call getUserSelection to get user's choic
                getUserSelection(selectionGroup);
                // if userSelection has been made
                if (userSelection != null) {
                    switch (userSelection) {
                        // user selects random activity, hide other unused widgets
                        case "1":
                            typeSpinner.setEnabled(false);
                            participantSlider.setEnabled(false);
                            typeSpinner.setVisibility(View.INVISIBLE);
                            participantSlider.setVisibility(View.INVISIBLE);
                            participantSlider.setValue(1);
                            break;
                        // user selects random activity with specified type, display the drop down list and hide other unused widgets
                        case "2":
                            typeSpinner.setEnabled(true);
                            participantSlider.setEnabled(false);
                            participantSlider.setValue(1);
                            // set up the drop down items for the spinner
                            setUpSpinnerChoice(typeSpinner);
                            typeSpinner.setVisibility(View.VISIBLE);
                            participantSlider.setVisibility(View.INVISIBLE);
                            break;
                        // user selects random activity with specified type, display the slider and hide other unused widgets
                        case "3":
                            typeSpinner.setEnabled(false);
                            participantSlider.setEnabled(true);
                            participantSlider.setVisibility(View.VISIBLE);
                            typeSpinner.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            }
        });


        // The submit button setOnClickListener will be triggered when the user clicks on submit
        submitButton.setOnClickListener(new View.OnClickListener(){
            /***
             * onClick --- actions to be performed when the user clicks submit
             * @param viewParam the current view
             */
            public void onClick(View viewParam) {
                // record username for customized recommendation
                String userName = ((EditText)findViewById(R.id.userName)).getText().toString();
                // if the user is not willing to record his / her name, by default the system recognizes he / she as anonymous user
                if (userName.isEmpty()) {
                    userName = "Anonymous User";
                }
                System.out.println("currentUser = " + userName);

                String toSearch = null; // the URL string to be specified when fetching
                // if userSelection has been made
                if (userSelection != null) {
                    switch(userSelection){
                        // user selects random activity
                        case "1":
                            toSearch = "activity";
                            break;
                        // user selects activity with a specified type
                        case "2":
                            if (typeSpinner != null) {
                                // get the result from drop down list
                                String type = getActivityType(typeSpinner);
                                toSearch = "activity?type=" + type;
                            } else {
                                System.out.println("Activity type not specified");
                            }
                            break;
                        // user selects activity with a specified number of participants
                        case "3":
                            if (participantSlider != null) {
                                // get the result from the slider
                                int numParticipants = getNumOfParticipants(participantSlider);
                                toSearch = "activity?participants=" + numParticipants;
                            } else {
                                System.out.println("Number of participants not selected");                       }
                            break;
                    }
                } else {
                    // if user did not make selection, by default choose option 1
                    System.out.println("User did not make selection");
                    toSearch = "activity";
                }

                // construct the searchTerm to be passed to get activity background thread
                String searchTerm = "username=" + userName + "&searchapi=" + toSearch;
                System.out.println("searchTerm " + searchTerm);

                GetActivity ga = new GetActivity();
                // Done asynchronously in another thread.  It calls ar.recommendationReady() in this thread when complete.
                ga.search(searchTerm, me, ar); }
        });
    }

    /***
     * getUserSelection --- get the user selection in the radio group
     * @param selection radio group
     */
    public void getUserSelection(RadioGroup selection) {
        // get selected radio button from radioGroup
        int selectedId = selection.getCheckedRadioButtonId();
        // if no button is selected, selectedId will be -1
        if (selectedId != -1) {
            // find the radiobutton by returned id
            RadioButton selectedOption = (RadioButton) findViewById(selectedId);
            // get the user selection
            switch(selectedOption.getId()){
                case R.id.option1:
                    userSelection = "1";
                    break;
                case R.id.option2:
                    userSelection = "2";
                    break;
                case R.id.option3:
                    userSelection = "3";
                    break;
            }
        } else {
            // if user did not select any radio buttons
                System.out.println("no radio button has been selected");
            }
        System.out.println("userSelection now is: " + userSelection);
        }


    /***
     * setUpSpinnerChoice ---configure the drop down list
     * @param typeSpinner the drop down widget
     */
    public void setUpSpinnerChoice(Spinner typeSpinner){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);
    }


    /***
     * getActivityType --- get the selected value from
     * @param typeSpinner drop down list
     * @return a string of the selected type
     */
    public String getActivityType(Spinner typeSpinner){
        return typeSpinner.getSelectedItem().toString();
    }

    /***
     * getNumOfParticipants --- get number of participants from the slider
     * @param participantSlider slider
     * @return an integer value of the selected option of the slider
     */
    public int getNumOfParticipants(Slider participantSlider){
        return (int)participantSlider.getValue();
    }

    /***
     * RecommendationReady --- this is called by the GetActivity object when the recommendation is ready.
     * This allows for passing back the response received from web servlet to update the output view
     * @param recommendation recommendation received in getActivity
     */
    public void RecommendationReady(String recommendation) {
        System.out.println("recommendation ready");

        // call resultDisplayString to create the text to be displayed on outputView
        String result = resultDisplayString(recommendation);
        // find the output view widget
        TextView outputView = (TextView)findViewById(R.id.output);

        // display the result
        System.out.println("printing result");
        outputView.setVisibility(View.VISIBLE);
        outputView.setText(result);

        // reset user input
        resetInput();
        outputView.invalidate();
    }

    /***
     * resultDisplayString --- create the text to be displayed on outputView
     * @param recommendation recommendation received in getActivity
     * @return a string to be displayed on outputView
     */
    public String resultDisplayString(String recommendation){
        // to store the final display string
        String result;
        // Create a Gson object
        Gson gson = new Gson();

        // if received error message from servlet
        System.out.println("recommendation received: " + recommendation);
        // handle web servlet response errors
        // (either empty string or {"error":"No activity found with the specified parameters"} returned)
        if (recommendation.isEmpty()|| recommendation.contains("No activity found with the specified parameters")){
            result = "Sorry, something went wrong and we could not find you an activity recommendation :(";
        } else {
            // the recommendation string contains username, activity, type, participants, price
            // convert the json string into Recommendation class and retrieve the needed info
            Recommendation r = gson.fromJson(recommendation, Recommendation.class);
            // create the result string to display
            result = "Here is the recommendation for: " + r.username + "\n" +
                     "Activity: " + r.activity + "\n" +
                     "Type: " + r.type + "\n" +
                     "Number of Participants: " + r.participants + "\n" +
                     "Estimated Price: " + r.price + "\n" +
                     "Enjoy :)";
        }
        return result;
    }

    /***
     * resetInput --- reset the user input after result is displayed
     */
    public void resetInput(){
        // find the widgets that need to be reset
        TextView userNameView = (EditText)findViewById(R.id.userName);
        RadioButton option1 = (RadioButton)findViewById(R.id.option1);
        RadioButton option2 = (RadioButton)findViewById(R.id.option2);
        RadioButton option3 = (RadioButton)findViewById(R.id.option3);
        Spinner typeSpinner = (Spinner)findViewById(R.id.activityType);
        Slider participantSlider = (Slider)findViewById(R.id.numParticipants);

        // clear username text
        userNameView.setText("");

        // clear the radio button selection
        option1.setChecked(false);
        option2.setChecked(false);
        option3.setChecked(false);

        // disable the drop down list
        typeSpinner.setVisibility(View.INVISIBLE);
        typeSpinner.setEnabled(false);

        // reset the slider and disable it
        participantSlider.setVisibility(View.INVISIBLE);
        participantSlider.setEnabled(false);
        participantSlider.setValue(1);

    }
}
