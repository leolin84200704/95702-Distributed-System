package ds;
/*
// Name: Leo Lin
// AndrewID: hungfanl
 */

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/*
 * The following WebServlet annotation gives instructions to the web container.
 * It states that when the user browses to the URL path /getAnInterestingPicture
 * then the servlet with the name InterestingPictureServlet should be used.
 *
 * This is the exact same as putting the following lines in the deployment
 * descriptor, web.xml:
 *  <servlet>
 *      <servlet-name>IPServlet</servlet-name>
 *      <servlet-class>InterestingPicture.InterestingPictureServlet</servlet-class>
 *  </servlet>
 *  <servlet-mapping>
 *      <servlet-name>IPServlet</servlet-name>
 *      <url-pattern>/getAnInterestingPicture</url-pattern>
 *  </servlet-mapping>
 */
@WebServlet(name = "AnswerClickerServlet",
        urlPatterns = {"/getP1T3Servlet", "/getResults"})
public class AnswerClickerServlet extends HttpServlet {

    AnswerClickerModel acm = null;  // The "business model" for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        acm = new AnswerClickerModel();
    }
    // This servlet will reply to HTTP POST requests via this doGet method
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        // get the search parameter if it exists
        String path = request.getServletPath();
        boolean mobile;
        String ans = request.getParameter("answer");
        // determine what type of device our user is
        String ua = request.getHeader("User-Agent");
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
        if(path.equals("/getResults")){
            int numA = acm.answer.get("A");
            int numB = acm.answer.get("B");
            int numC = acm.answer.get("C");
            int numD = acm.answer.get("D");
            int sum = numA + numB + numC + numD;
            request.setAttribute("numA", numA);
            request.setAttribute("numB", numB);
            request.setAttribute("numC", numC);
            request.setAttribute("numD", numD);
            request.setAttribute("sum", sum);
            nextView = "check.jsp";
        }
        else{

            /*
             * Check if the search parameter is present.
             * If not, then give the user instructions and prompt for a search string.
             * If there is a search parameter, then do the search and return the result.
             */
            if (ans != null) {
                String picSize = (mobile) ? "mobile" : "desktop";
                /*
                 * Attributes on the request object can be used to pass data to
                 * the view.  These attributes are name/value pairs, where the name
                 * is a String object.  Here the pictureURL is passed to the view
                 * after it is returned from the model interestingPictureSize method.
                 */
                acm.addNAnswer(ans);

                request.setAttribute("answer", ans);
                // Pass the user search string (pictureTag) also to the view.
                nextView = "result.jsp";
            } else {
                // no search parameter so choose the prompt view
                nextView = "prompt.jsp";
            }
        }
        // Transfer control over the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        // get the search parameter if it exists
        String path = request.getServletPath();
        boolean mobile;
        String ans = request.getParameter("answer");
        // determine what type of device our user is
        String ua = request.getHeader("User-Agent");
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
        // if the user input getResults url, then it will leads to another page for the result.
        if(path.equals("/getResults")){
            int numA = acm.answer.get("A");
            int numB = acm.answer.get("B");
            int numC = acm.answer.get("C");
            int numD = acm.answer.get("D");
            int sum = numA + numB + numC + numD;
            request.setAttribute("numA", numA);
            request.setAttribute("numB", numB);
            request.setAttribute("numC", numC);
            request.setAttribute("numD", numD);
            request.setAttribute("sum", sum);
            nextView = "check.jsp";
        }
        else{

            /*
             * Check if the search parameter is present.
             * If not, then give the user instructions and prompt for a search string.
             * If there is a search parameter, then do the search and return the result.
             */
            if (ans != null) {
                String picSize = (mobile) ? "mobile" : "desktop";
                /*
                 * Attributes on the request object can be used to pass data to
                 * the view.  These attributes are name/value pairs, where the name
                 * is a String object.  Here the pictureURL is passed to the view
                 * after it is returned from the model interestingPictureSize method.
                 */
                acm.addNAnswer(ans);

                request.setAttribute("answer", ans);
                // Pass the user search string (pictureTag) also to the view.
                nextView = "result.jsp";
            } else {
                // no search parameter so choose the prompt view
                nextView = "prompt.jsp";
            }
        }
        // Transfer control over the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
}

