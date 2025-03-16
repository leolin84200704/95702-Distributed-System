// Name: Hsiu-Yuan Yang
// AndrewID: hsiuyuay
// Email: hsiuyuay@andrew.cmu.edu
package ds.activity.recommendation;

// load stuff we need
import android.app.Activity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/***
 * This class provides capabilities to ask for an activity recommendation from the server given a user request.
 * The method "search" is the entry to the class. Network operations cannot be done from the UI thread,
 * therefore this class makes use of inner class BackgroundTask that will do the network operations in a separate worker thread.
 * However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the recommendationReady method to do the update.
 * 
 * Method BackgroundTask.doInBackground( ) does the background work
 * Method BackgroundTask.onPostExecute( ) is called when the background work is
 *    done; it calls *back* to ActivityRecommendation to report the results
 */
public class GetActivity {
    // for callback
    ActivityRecommendation ar = null;
    // ping web servlet with this search term
    String searchTerm = null;
    // recommendation returned from web servlet
    String recommendation = null;

    /***
     * search --- entry of this class
     * @param searchTerm: the user request to pass to web servlet
     * @param activity:  the UI thread activity
     * @param ar: the callback method's class; here, it will be ar.recommendationReady( )
     */
    public void search(String searchTerm, Activity activity, ActivityRecommendation ar) {
        System.out.println("get activity recommendation");
        this.ar = ar;
        this.searchTerm = searchTerm;
        new BackgroundTask(activity).execute();
    }


    /***
     * code referenced from AndroidInterestingPicture lab
     *
     * class BackgroundTask
     * Implements a background thread for a long running task that should not be
     * performed on the UI thread. It creates a new Thread object, then calls doInBackground() to
     * actually do the work. When done, it calls onPostExecute(), which runs
     * on the UI thread to update some UI widget
     *
     * Adapted from one of the answers in
     * https://stackoverflow.com/questions/58767733/the-asynctask-api-is-deprecated-in-android-11-what-are-the-alternatives
     * Modified by Barrett
     */
    private class BackgroundTask {
        // The UI thread
        private Activity activity;
        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        /***
         * execute --- initiate
         */
        private void execute(){
            // There could be more setup here, which is why
            //    startBackground is not called directly
            startBackground();
        }

        /***
         * startBackground --- creates a new thread for background search / differentiate from UI thread
         */
        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {
                    // call background thread for tasks
                    doInBackground();
                    // onPostExecute runs on the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }


        /***
         * doInBackground --- implements stuff we need background thread to finish
         */
        private void doInBackground() {
            System.out.println("doInBackground");
            recommendation = backgroundSearch(searchTerm);
        }

        /***
         * onPostExecute --- runs on the UI thread after the background thread completes
         */
        public void onPostExecute() {
            System.out.println("on post execute");
            // pass the received recommendation to the model's recommendation ready method
            ar.RecommendationReady(recommendation);
        }

        /***
         * backgroundSearch --- Make an HTTP request to a given URL
         *
         * @param searchTerm The search term to be sent to the web servlet
         * @return A string of the response from the HTTP GET.  This is identical
         * to what would be returned from using curl on the command line.
         */
        public String backgroundSearch(String searchTerm) {
            // to store connection status
            int status;
            // to store the response received from servlet
            String response = "";
            try {
                // replace spaces for encoded search string
                String searchTermEncoded = searchTerm.replace(" ", "%20");
                System.out.println("searchTermEncoded "+searchTermEncoded);
                // create the url string with codespace
                String urlString = "https://hsiuyuay-fuzzy-rotary-phone-4xg6pq44g5wcjw97-8080.preview.app.github.dev/getActivity?" + searchTermEncoded;

                URL url = new URL(urlString);
                /*
                 * Create an HttpURLConnection.  This is useful for setting headers
                 * and for getting the path of the resource that is returned (which
                 * may be different than the URL above if redirected).
                 * HttpsURLConnection (with an "s") can be used if required by the site.
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // get the response status
                status = connection.getResponseCode();
                System.out.println("connection status "+ status);

                // handle network error in app
                if (status == 200) {
                    // wait for response
                    // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    System.out.println("Using buffered reader");
                    String str;
                    // Read each line of "in" until done, adding each to "response"
                    while ((str = in.readLine()) != null) {
                        // str is one line of text readLine() strips newline characters
                        response += str;
                    }
                    System.out.println("response received from server: " + response);
                    in.close();
                } else {
                    System.out.println("Network error occurred: cannot connect server");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Eeek, an exception");
            }
            return response;
        }



    }
}

