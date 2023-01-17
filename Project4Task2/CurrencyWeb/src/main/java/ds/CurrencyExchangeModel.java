package ds;
/*
 * @author Leo, Yash
 *
 * The servlet is acting as the model that provides the following functions.
 * 1. With some parameters, ask for the result from a third party api and extract essential information and return.
 * 2. Whenever the function is called, the function record the data in MongoDB.
 * 3. Ask for the data from MongoDB and provide the most request currency and date.
 */

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
import java.util.*;

public class CurrencyExchangeModel {
    static String[] currencies = {"USD", "AUD","BGN","BRL","CAD","CHF","CNY","CZK","DKK","EUR","GBP","HKD","HRK"
            ,"HUF","IDR","ILS","INR","ISK","JPY","KRW","MXN","MYR","NOK","NZD","PHP","PLN","RON","SEK"
            ,"SGD","THB","TRY","ZAR"};
    static PriorityQueue<String> fromQueue = null;
    static PriorityQueue<String> toQueue = null;
    static PriorityQueue<String> freqDatesQueue =null;
    static ArrayList<String> sb = null;

    /**
     * Ask for the result from a 3rd party api and record the request to MongoDB.
     * @param currencyFrom The currency user has.
     * @param currencyTo The currency user wants.
     * @param amountString The amount that the user wants to exchange.
     * @return The Json Object with date and the exchangeAmount.
     */
    public static String getInfo(String currencyFrom, String currencyTo, String amountString) {
        int currency_from_index;
        int currency_to_index;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM, dd, yyyy");

        currency_from_index = getCurrency(currencyFrom);
        currency_to_index = getCurrency(currencyTo);
        String exchangeAmount = null;
        if (currency_from_index != currency_to_index) {
            String address = "https://api.frankfurter.app/latest?amount=" + amountString + "&from=" + currencies[currency_from_index] +
                    "&to=" + currencies[currency_to_index];
            String result = fetch(address);
            JSONObject output_object = (JSONObject) JSONValue.parse(result);
            exchangeAmount = ((JSONObject) output_object.get("rates")).get(currencies[currency_to_index]).toString();
        }
        else {
            exchangeAmount = amountString;
        }

        Double exchange = Double.valueOf(exchangeAmount);
        Double amount = Double.valueOf(amountString);
        String time = sdf.format(new Date(System.currentTimeMillis()));
        getMongoDB();
        pushToMongo(currency_from_index, currency_to_index, amount, exchange, time);
        return getData(currency_from_index, currency_to_index, amount, exchange, time);
    }

    /**
     * Determine if the string is a valid currency.
     * @param s The string to be determined.
     * @return If the string is a valid currency.
     */
    static public boolean contains(String s) {
        for (int i = 0; i < currencies.length; i++) {
            if(currencies[i].equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    /**
     * Determine if the string is a valid number.
     * @param s The string to be determined.
     * @return If the string is a valid number.
     */
    static public boolean isNumber(String s) {
        boolean isNumber = true;

        try {
            Double.valueOf(s);
        } catch (NumberFormatException e) {
            isNumber = false;
        }
        return isNumber;
    }

    /**
     * Determine the position of a type of currency.
     * @param s The currency to be checked.
     * @return The position of that currency.
     */
    static public int getCurrency(String s) {
        for (int i = 0; i < currencies.length; i++) {
            if(currencies[i].equalsIgnoreCase(s)) return i;
        }
        return -1;
    }

    /**
     * Determine the position of a type of currency.
     * @param urlString URL string of the website.
     * @return The position of that currency.
     */
    static private String fetch(String urlString) {
        String response = "";
        try {
            System.out.println(urlString);
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

    /**
     * Push the record to MongoDB.
     * @param currency_from_index The index of currency that the user has.
     * @param currency_to_index The index of currency that the user wants.
     * @param amount The amount that the user wants to exchange.
     * @param exchange The amount that the user gets.
     * @param time The time the transaction happens.
     */
    static private void pushToMongo(int currency_from_index, int currency_to_index, double amount, double exchange, String time) {
        String username = "AppUser";

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
        doc.append("transaction time", time);
        col.insertOne(doc);

    }

    /**
     * Generate a reply api.
     * @param currency_from_index The index of currency that the user has.
     * @param currency_to_index The index of currency that the user wants.
     * @param amount The amount that the user wants to exchange.
     * @param exchange The amount that the user gets.
     * @param time The time the transaction happens.
     * @return the string representation of a JSONObject
     */
    static private String getData(int currency_from_index, int currency_to_index, double amount, double exchange, String time) {
        JSONObject returnObject = new JSONObject();
        returnObject.put("Base", currencies[currency_from_index]);
        returnObject.put("Amount", amount);
        returnObject.put("ToCurrency", currencies[currency_to_index]);
        returnObject.put("Exchange Amount", exchange);
        returnObject.put("Transaction Time", time);
        return returnObject.toString();
    }

    /**
     * Get all the data recorded in MongoDB.
     */
    static public void getMongoDB() {
        MongoClient client = MongoClients
                .create("mongodb://leolin84200704:leolin84200704@ac-hdxdkkm-shard-00-00.grw8a3t.mongodb.net:27017" +
                        ",ac-hdxdkkm-shard-00-01.grw8a3t.mongodb.net:27017," +
                        "ac-hdxdkkm-shard-00-02.grw8a3t.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
        MongoDatabase db = client.getDatabase("Project4");
        MongoCollection col = db.getCollection("CurrencyInquiryHistory");
        FindIterable<Document> iterDoc = col.find();
        sb = new ArrayList<>();
        MongoCursor<Document> cursor = iterDoc.iterator();
        createFrequentUse(cursor);
        return;
    }

    /**
     * Put the data into the Map.
     * @param cursor The cursor of the data in MongoDB.
     */
    static private void createFrequentUse(MongoCursor<Document> cursor) {
        Map<String,Integer> fromMap = new HashMap<>();
        Map<String,Integer> toMap = new HashMap<>();
        Map<String,Integer> datesMap = new HashMap<>();
        fromQueue =  new PriorityQueue<>();
        toQueue = new PriorityQueue<>();
        freqDatesQueue = new PriorityQueue<>();

        while (cursor.hasNext()) {
            Document d = cursor.next();
            fromMap.putIfAbsent((String) d.get("from"),0);
            fromMap.put((String) d.get("from"),fromMap.get(d.get("from"))+1);
            toMap.putIfAbsent((String) d.get("to"),0);
            toMap.put((String) d.get("to"),toMap.get(d.get("to"))+1);
            datesMap.putIfAbsent((String) d.get("transaction time"),0);
            datesMap.put((String) d.get("transaction time"),datesMap.get(d.get("transaction time"))+1);
            sb.add(d.get("transaction time") + "     " + d.get("name") + "     " + d.get("from") + "     " +
                    d.get("amount") + "     " + d.get("to") + "     " + d.get("exchange_amount"));
        }
        for(String s: fromMap.keySet()) {
            fromQueue.add(s);
            if(fromQueue.size()>3) {
                fromQueue.poll();
            }
        }
        for(String s: toMap.keySet()) {
            toQueue.add(s);
            if(toQueue.size()>3) {
                toQueue.poll();
            }
        }
        for(String s: datesMap.keySet()) {
            freqDatesQueue.add(s);
            if(freqDatesQueue.size()>3) {
                freqDatesQueue.poll();
            }
        }
    }

    /**
     * Provide the most exchanged currency.
     * @return the next most exchanged currency.
     */
    public static String fetchFrequentFromCurrency() {
        return fromQueue.poll();
    }

    /**
     * Provide the most wanted currency.
     * @return the next most wanted currency.
     */
    public static String fetchFrequentToCurrency() {
        return toQueue.poll();
    }

    /**
     * Provide the most exchanged date.
     * @return the next most exchanged date.
     */
    public static String fetchFrequentDates() {
        return freqDatesQueue.poll();
    }

}
