package ds;
/*
 * @author Leo, Yash
 *
 * The servlet is acting as the controller.
 * There are three views - prompt.jsp, result.jsp., and dashboard.jsp
 * It decides between the two by determining if there is a search parameter or
 * not. If there is no parameter, then it uses the prompt.jsp view, as a
 * starting place. If there is a search parameter, then it searches for a
 * picture and uses the result.jsp view.
 * The developer can check the backstage data with dashboard.jsp
 */


import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


/*
 * The following WebServlet annotation gives instructions to the web container.
 * It states that when the user browses to the URL path /CurrencyExchange
 * then the servlet with the name CurrencyExchange should be used.
 * If the developer browses to the URL path /getDashBoard
 * then the servlet with the name getDashBoard should be used.
 */
@WebServlet(name = "CurrencyExchangeServlet",
        urlPatterns = {"/CurrencyExchange","/getDashBoard"})
public class CurrencyExchangeServlet extends HttpServlet {


    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String nextView="";
        if(path.contains("CurrencyExchange")) {

            // get the search parameter if it exists
            String currencyFrom = request.getParameter("currencyFrom");
            String currencyTo = request.getParameter("currencyTo");
            String amountString = request.getParameter("amountString");


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


            /*
             * Check if the search parameter is present.
             * If not, then give the user instructions and prompt for a search string.
             * If there is a search parameter, then do the search and return the result.
             */

            if (CurrencyExchangeModel.contains(currencyFrom) && CurrencyExchangeModel.contains(currencyTo) && CurrencyExchangeModel.isNumber(amountString)) {

                String result = CurrencyExchangeModel.getInfo(currencyFrom, currencyTo, amountString);
                // Set the api to result.jsp
                request.setAttribute("result",result);
                nextView = "result.jsp";
            } else {
                // no search parameter so choose the prompt view
                nextView = "prompt.jsp";
            }

        }else if(path.contains("getDashBoard")){
            CurrencyExchangeModel.getMongoDB();
            request.setAttribute("result", CurrencyExchangeModel.sb);
            ArrayList<String> fromStrings = new ArrayList<>();
            ArrayList<String> toStrings = new ArrayList<>();
            ArrayList<String> freqDates = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                String str = CurrencyExchangeModel.fetchFrequentFromCurrency();
                if (str!=null) {
                    fromStrings.add(str);
                }
            }
            request.setAttribute("fromArray", fromStrings);
            for(int i = 0; i < 10; i++) {
                String str = CurrencyExchangeModel.fetchFrequentToCurrency();
                if(str!=null) {
                    toStrings.add(str);
                }
            }
            request.setAttribute("toArray", toStrings);
            for(int i=0;i< 10;i++) {
                String str = CurrencyExchangeModel.fetchFrequentDates();
                if(str!=null) {
                    freqDates.add(str);
                }
            }
            request.setAttribute("freqDates", freqDates);
            nextView = "dashboard.jsp";
        }

        // Transfer control over the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
}

