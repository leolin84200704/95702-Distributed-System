import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.*;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Scanner;

public class MongoDB {
    static String[] currencies = {"USD", "AUD","BGN","BRL","CAD","CHF","CNY","CZK","DKK","EUR","GBP","HKD","HRK"
            ,"HUF","IDR","ILS","INR","ISK","JPY","KRW","MXN","MYR","NOK","NZD","PHP","PLN","RON","SEK"
            ,"SGD","THB","TRY","ZAR"};
    public static void main(String[] args) {
        Scanner readInput = new Scanner(System.in);
        int currency_from_index;
        int currency_to_index;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

        String username;

        System.out.println("Please insert username");
        username = readInput.nextLine();
        for (int i = 0; i < currencies.length; i++) {
            System.out.print(i+1 + ": " + currencies[i] + "\t");
            if (i % 5 == 4) {
                System.out.println();
            }
        }
        System.out.println("\nPlease select the currency you have");
        currency_from_index = readInput.nextInt()-1;
        if (currency_from_index < 0 || currency_from_index >= currencies.length) {
            System.out.println("Invalid input, program shutting down");
            return;
        }
        System.out.println("How much money of the currency do you have?");
        double amount = readInput.nextDouble();
        System.out.println("Please select the currency you need");
        currency_to_index = readInput.nextInt()-1;
        if (currency_to_index < 0 || currency_to_index >= currencies.length) {
            System.out.println("Invalid input, program shutting down");
            return;
        }
        String address = "https://api.frankfurter.app/latest?amount=" + amount + "&from=" + currencies[currency_from_index] +
                "&to=" + currencies[currency_to_index];
        System.out.println(address);
        JSONObject output_object = (JSONObject) JSONValue.parse(fetch(address));
        System.out.println(output_object);
        String date = (String) output_object.get("date");
        Double exchange = (Double) ((JSONObject) output_object.get("rates")).get(currencies[currency_to_index]);
        System.out.println("You get " + exchange + " " + currencies[currency_to_index] + " for " + amount + " " + currencies[currency_from_index] + " on "
                + date);
        MongoClient client = MongoClients
                .create("mongodb://leolin84200704:leolin84200704@ac-hdxdkkm-shard-00-00.grw8a3t.mongodb.net:27017" +
                        ",ac-hdxdkkm-shard-00-01.grw8a3t.mongodb.net:27017," +
                        "ac-hdxdkkm-shard-00-02.grw8a3t.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
        MongoDatabase db = client.getDatabase("Project4");
        MongoCollection col = db.getCollection("CurrencyInquiryHistory");
        Document doc = new Document("name", username);
        doc.append("from", currencies[currency_from_index]);
        doc.append("to", currencies[currency_to_index]);
        doc.append("amount", amount);
        doc.append("exchange_amount", exchange);
        doc.append("transaction time", sdf.format(new Date(System.currentTimeMillis())));
        col.insertOne(doc);


        FindIterable<Document> iterDoc = col.find();
        MongoCursor<Document> cursor = iterDoc.iterator();
        while (cursor.hasNext()) {
            Document d = cursor.next();
            System.out.println("User " + d.get("name") + " transfer " + d.get("amount") + " "
             + d.get("from") + " to " + d.get("to") + " and get " + d.get("exchange_amount"));
        }
    }

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
