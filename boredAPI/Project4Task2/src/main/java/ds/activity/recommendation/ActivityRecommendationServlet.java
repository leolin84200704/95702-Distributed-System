// Name: Hsiu-Yuan Yang
// AndrewID: hsiuyuay
// Email: hsiuyuay@andrew.cmu.edu
package ds.activity.recommendation;

/*
 * @author Hsiu-Yuan Yang
 *
 * This file is the Controller component of the MVC.
 * The welcome-file in the deployment descriptor (web.xml) points
 * to this servlet.  So it is also the starting place for the web
 * application.
 *
 * The view component is dashboard.jsp, which displays the operations analytics and logs of the web servlet.
 * According to the user input, it then gets the activity recommendation from BoredAPI and returns an activity recommendation.
 * The model is provided by ActivityRecommendationModel.
 */

// load stuff we need
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/*
 * The servlet accepts the users http request and passes the information to model for processing. It then sends back the response.
 */
@WebServlet(name = "ActivityRecommendationServlet",
        urlPatterns = {"/getActivity", "/getDashboard"})
public class ActivityRecommendationServlet extends HttpServlet {
    // The "business model" for this app
    private ActivityRecommendationModel arm = null;


    // Initiate this servlet by instantiating the model that it will use
    @Override
    public void init() {
        arm = new ActivityRecommendationModel();
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    // code referenced from Interesting Picture
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        // determine what type of device our user is
        String ua = request.getHeader("User-Agent");

        boolean mobile;
        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            mobile = true;
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }

        // to get the servlet path
        String path = request.getServletPath();
        // if user wants to view dashboard results
        if (path.contains("/getDashboard")){
            // get log table
            String logValues = arm.generateDashboard(arm.connectMongoDB());
            request.setAttribute("logValues", logValues);

            // get top user (if user map is empty, top user count will be -1)
            Collection<List<String>> topUsers = arm.getTopUser().values();
            Set<Integer> topUserCount = arm.getTopUser().keySet();
            request.setAttribute("topUser", topUsers.toString().replace("[","").replace("]", ""));
            request.setAttribute("topUserCount", topUserCount.toString().replace("[","").replace("]",""));

            // get top request dates (if request dates map is empty, top date count will be -1)
            Collection<List<String>> topRequestDate = arm.getTopRequestDates().values();
            Set<Integer> topDateCount = arm.getTopRequestDates().keySet();
            request.setAttribute("topRequestDate", topRequestDate.toString().replace("[","").replace("]", ""));
            request.setAttribute("topDateCount", topDateCount.toString().replace("[","").replace("]", ""));

            // get response successful rate (if response successful rates map is empty, the rate will be -1)
            Double successfulRate = arm.getResponseSuccessfulRate();
            request.setAttribute("successfulRate", String.valueOf(Math.round(successfulRate)));

            // get top activity types (if activity types map is empty, top activity type count will be -1)
            Collection<List<String>> topActivityType = arm.getTopActivityType().values();
            Set<Integer> topTypeCount = arm.getTopActivityType().keySet();
            request.setAttribute("topActivityType", topActivityType.toString().replace("[","").replace("]", ""));
            request.setAttribute("topTypeCount", topTypeCount.toString().replace("[","").replace("]", ""));

            // get average process time (if process times map is empty, average process time will be -1)
            long avgProcessTime = arm.getAverageProcessTime();
            request.setAttribute("avgProcessTime", String.valueOf(avgProcessTime));

            // to redirect to dashboardView
            String dashboardView = "dashboard.jsp";
            // Transfer control over the correct "view"
            RequestDispatcher view = request.getRequestDispatcher(dashboardView);
            view.forward(request, response);

        } else if (path.contains("/getActivity")) {
            // user sending request from app
            // get the username from app
            String username = request.getParameter("username");
            // get the searchTerm from app
            String searchTerm = request.getParameter("searchapi");

            // if app user did send out a valid search term
            if (!searchTerm.isEmpty()) {
                // record current timestamp
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                // start the timer for processing time
                long start = System.currentTimeMillis();

                // create the URL string to send to API
                String searchURL = "https://www.boredapi.com/api/" + searchTerm;
                // call model's fetch method to get the reply from API
                String replyFromAPI = arm.fetch(searchURL);

                // construct the json string to be sent to app
                String responseToApp = arm.getRecommendationResponse(username, replyFromAPI);

                // send the response to app
                response.setContentType("text/html");
                response.getWriter().println(responseToApp);
                response.getWriter().flush();
                System.out.println("Sent out");

                // set end time for calculating the processing time
                long end = System.currentTimeMillis();
                long processTime = end - start;

                // log the record to MongoDB
                arm.logToMongoDB(username, timestamp, responseToApp, processTime);
            } else {
                System.out.println("no search parameter received from request");
            }

        } else {
            System.out.println("Path is suspicious");
        }
    }
}





