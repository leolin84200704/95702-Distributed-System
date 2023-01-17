package ds.helloworld;


import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "The SHA256 Hash of Hello World is ";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        try {
            // Access MessageDigest class for SHA-265
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Compte the digest
            md.update("Hello World".getBytes());
            String hash = bytesToHex(md.digest());
            // echo to console
            System.out.println(hash);
            // get a print writer from the response object
            PrintWriter out = response.getWriter();
            // send an html document to caller
            out.println("<html><body>");
            // compute digest, convert to hex, send back to caller
            out.println("<h1>" + message + hash + "</h1>");
            out.println("</body></html>");
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("No SHA-256 available" + e);
        }
    }

    public void destroy() {
    }

    // Code from stack overflow
    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    // Returns a hex string given an array of bytes
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}