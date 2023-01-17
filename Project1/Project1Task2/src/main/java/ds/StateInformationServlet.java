package ds;
// Name: Leo Lin
// AndrewID: hungfanl

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


@WebServlet(name = "StateInformationServlet",
        urlPatterns = {"/getAnStateInformation"})
public class StateInformationServlet extends HttpServlet {

    StateInformationModel sim = null;  // The "business model" for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        sim = new StateInformationModel();
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // get the search parameter if it exists
        String search = request.getParameter("state");
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

        String nextView;
        /*
         * Check if the search parameter is present.
         * If not, then give the user instructions and prompt for a search string.
         * If there is a search parameter, then do the search and return the result.
         */
        if (search != null) {
            String picSize = (mobile) ? "mobile" : "desktop";
            // use model to do the search and choose the result view
            String state = search;
            String flagURL = sim.doFlagSearch(search);
            String population = sim.getPopulation(search);
            String nickname = sim.getNickName(search);
            String capital = sim.getCapital(search);
            String song = sim.getSong(search);
            String flowerURL = sim.doFlowerSearch(search);
            /*
             * Attributes on the request object can be used to pass data to
             * the view.  These attributes are name/value pairs, where the name
             * is a String object.  Here the pictureURL is passed to the view
             * after it is returned from the model interestingPictureSize method.
             */
            request.setAttribute("state",state);
            request.setAttribute("population", population);
            request.setAttribute("nickname", nickname);
            request.setAttribute("capital", capital);
            request.setAttribute("song", song);
            request.setAttribute("flowerURL", flowerURL);
            request.setAttribute("flagURL", flagURL);
            // Pass the user search string (pictureTag) also to the view.
            nextView = "result.jsp";
        } else {
            // no search parameter so choose the prompt view
            nextView = "prompt.jsp";
        }
        // Transfer control over the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
}

