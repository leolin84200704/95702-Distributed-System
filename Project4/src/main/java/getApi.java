import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.io.IOException;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class getApi {
    public static void main(String[] args) {
        Scanner readInput = new Scanner(System.in);
        Double amount = 2000.0;
        String from_currency= "AUD";
        String to_currency = "EUR";
        String address = "https://api.frankfurter.app/latest?amount=" + amount + "&from=" + from_currency +
                "&to=" + to_currency;
        System.out.println(address);
        JSONObject output_object = (JSONObject) JSONValue.parse(fetch(address));
        System.out.println(output_object);
        String date = (String) output_object.get("date");
        Double exchange = (Double) ((JSONObject) output_object.get("rates")).get(to_currency);
        System.out.println("You get " + exchange + " " + to_currency + " for " + amount + " " + from_currency + " on "
        + date);

    }
    // This function is from the resource provided in class.
    static private String fetch(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which
             * may be different from the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
        }
        return response;
    }
}


