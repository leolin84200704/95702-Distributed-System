package hash.compute;
// Name: Leo Lin
// AndrewID: hungfanl

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.security.MessageDigest;
import jakarta.servlet.ServletException;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.*;

@WebServlet(name = "computeHashes", urlPatterns = {"/computeHash"})

public class ComputeHashes extends HttpServlet {


    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String original_value = request.getParameter("hash_value_name");
        String hash_way = request.getParameter("hash_way");
        try {
            String hash_hexadecimal;
            String hash_64notation;
            MessageDigest md;
            if(hash_way.equals("SHA-256")){
                // Compute SHA-265 code for the input
                md = MessageDigest.getInstance("SHA-256");
            }
            else{
                // Compute MD5 code for the input
                md = MessageDigest.getInstance("MD5");
            }
            md.update(original_value.getBytes());
            hash_64notation = DatatypeConverter.printBase64Binary(md.digest());
            hash_hexadecimal = DatatypeConverter.printHexBinary(md.digest());
            // echo to console
            System.out.println(hash_64notation);
            System.out.println(hash_hexadecimal);
            // get a print writer from the response object
            PrintWriter out = response.getWriter();
            // send a html document to caller
            out.println("<html><body>");
            // compute digest, convert to hex, send back to caller
            out.println("<h1>" + "The " + hash_way + " Hash of " + original_value + "</h1>");
            out.println("<h1>" + "Hexadecimal: " + hash_hexadecimal + "</h1>");
            out.println("<h1>" + "64 notation: " + hash_64notation + "</h1>");
            out.println("</body></html>");
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("No Hash available" + e);
        }
    }

    public void destroy() {
    }

}