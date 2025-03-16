// Name: Hsiu-Yuan Yang
// AndrewID: hsiuyuay
// Email: hsiuyuay@andrew.cmu.edu
package ds.activity.recommendation;

/*
 * @author Hsiu-Yuan Yang
 *
 * This file is the Model component of the MVC, and it models the business
 * logic for the activity recommendation system.  In this case, the business logic involves
 * massaging the reply from API and building the response to be sent to the app, as well as
 * MongoDB operations including logging, generating statistics and preparing for the dashboard.
 */

// load stuff we need
import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import org.bson.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***
 * The ActivityRecommendationModel fetches the API response from BoredAPI with the user request passed in by the servlet.
 * It then constructs the response message and passes the recommendations back to the servlet.
 * For MongoDB part, the model first connects to MongoDB, logs the operations, and prepares the statistics for the dashboard when requested.
 */
public class ActivityRecommendationModel {
    // to store the user records logged on MongoDB
    private static Map<String, Integer> users = new HashMap<>();
    // to store the user request dates logged on MongoDB
    private static Map<String, Integer> requestDates = new HashMap<>();
    // to store the response generation statuses logged on MongoDB
    private static Map<String, Integer> responseGenerateSuccess = new HashMap<>();
    // to store the activity types logged on MongoDB
    private static Map<String, Integer> types = new HashMap<>();
    // to store the process times logged on MongoDB
    private static Map<String, Integer> processTimes = new HashMap<>();
    // to store the logs logged on MongoDB for display
    private static StringBuilder tableValues = new StringBuilder();

    /***
     * getRecommendationResponse --- massages the json response from api and prepares the json string to be sent to app,
     * also records the responseToApp into MongoDB
     * @param username the app user's name, which will also be recorded onto MongoDB
     * @param replyFromAPI the response fetched from BoredAPI
     * @return responseToApp: a string which contains the needed information built from replyFromAPI to be sent back to user
     */
    public static String getRecommendationResponse(String username, String replyFromAPI) {
        // to store response to app
        String responseToApp;
        // create a Gson object to parse the JSON string received
        Gson gson = new Gson();

        // if API returns error message ({"error":"No activity found with the specified parameters"})
        if (replyFromAPI.contains("No activity found with the specified parameters")) {
            // just send the api response to app
            responseToApp = gson.toJson(replyFromAPI);
        } else {
            // construct the response message with the needed parameters
            // parses the JSON string from API
            RecommendationFromAPI recommendationFromAPI = gson.fromJson(replyFromAPI, RecommendationFromAPI.class);
            // extract needed info from the api json string
            String activity = recommendationFromAPI.activity;
            String type = recommendationFromAPI.type;
            Integer participants = recommendationFromAPI.participants;
            Double price = recommendationFromAPI.price;

            // display the info to be put into the response to app
            System.out.println("Activity: " + activity);
            System.out.println("Type:" + type);
            System.out.println("Number of participants: " + participants);
            System.out.println("Estimated Price: " + price);

            // constructs another JSON string to send back to app
            RecommendationToApp recommendationToApp = new RecommendationToApp(username, activity, type,
                    participants, price);
            responseToApp = gson.toJson(recommendationToApp);

        }
        System.out.println("From model: reply to send to app: " + responseToApp);
        return responseToApp;
    }

    /***
     * fetch --- makes an HTTP request to BoredAPI
     *
     * @param urlString The URL of the request
     * @return A string of the response from the HTTP GET.  This is identical
     * to what would be returned from using curl on the command line.
     */
    public static String fetch(String urlString) {
        // to store response from BoredAPI
        String response = "";
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which
             * may be different than the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the api
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            System.out.println("response from api: " + response);
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
            System.out.println(e.getMessage());
        }
        return response;
    }

    /***
     * connectMongoDB --- connects to MongoDB and fetch the MongoCollection within the database
     * @return the MongoCollection where documents should be stored in
     */
    // code referenced from MongoDB application connection example
    public static MongoCollection<Document> connectMongoDB() {
        // replace the placeholder with MongoDB deployment's connection string
        String uri = "mongodb://hsiuyuay:DSProject4@ac-cwk6wyo-shard-00-01.xstvb87.mongodb.net:27017,ac-cwk6wyo-shard-00-00.xstvb87.mongodb.net:27017,ac-cwk6wyo-shard-00-02.xstvb87.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";
        // connect to MongoDB
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        System.out.println("connection string created");
        MongoClient mongoClient = MongoClients.create(settings);
        // connect to MongoDB database
        MongoDatabase database = mongoClient.getDatabase("DSProject4");
        // retrieve the collection
        MongoCollection<Document> collection = database.getCollection("ActivityRecommendationLog");
        return collection;
    }

    /***
     * logToMongoDB --- to decide which MongoDB logging function to call for and generate response successful status
     * @param username name of the user who submitted user request
     * @param timestamp the time when the user request was made
     * @param responseToApp the response string to be sent to the app
     * @param processTime the processing time for the system to handle the user request
     */
    public void logToMongoDB(String username, Timestamp timestamp, String responseToApp, long processTime) {
        String responseGenerated;
        // if error message was received from API and the servlet is going to send error message to app
        if (responseToApp.contains("No activity found with the specified parameters")) {
            responseGenerated = "No";
            // record the error status to MongoDB
            System.out.println("logToMongoDB error");
            sendToMongoError(connectMongoDB(), username, timestamp, responseGenerated, processTime);

        } else { // construct the response message with the needed parameters
            responseGenerated = "Yes";
            // record the responseToApp to MongoDB
            sendToMongoSuccess(connectMongoDB(), username, timestamp, responseGenerated, responseToApp, processTime);

        }
    }

    /***
     * sendToMongoSuccess --- to log the success records into MongoDB
     * @param collection the location where the document should be logged on
     * @param username name of the user who submitted user request
     * @param timestamp the time when the user request was made
     * @param responseGenerated status of whether the API found a recommendation
     * @param responseToApp the response string to be sent to the app
     * @param processTime the processing time for the system to handle the user request
     */
    public static void sendToMongoSuccess(MongoCollection<Document> collection, String username, Timestamp timestamp, String
            responseGenerated, String responseToApp, long processTime) {
        // create a new document
        Document doc = new Document("name", username);
        doc.append("timestamp", String.valueOf(timestamp));
        doc.append("response_generated", responseGenerated);
        // create Gson object to extract from JSON string
        Gson gson = new Gson();
        // retrieve the needed data
        RecommendationToApp recommendationToApp = gson.fromJson(responseToApp, RecommendationToApp.class);
        doc.append("activity", recommendationToApp.activity);
        doc.append("type", recommendationToApp.type);
        doc.append("num_of_participants", String.valueOf(recommendationToApp.participants));
        doc.append("estimated_price", String.valueOf(recommendationToApp.price));
        doc.append("process_time", String.valueOf(processTime));
        // store data into collection
        collection.insertOne(doc);
    }

    /***
     * sendToMongoError---log the error records into MongoDB
     * @param collection the location where the document should be logged on
     * @param username name of the user who submitted user request
     * @param timestamp the time when the user request was made
     * @param responseGenerated status of whether the API found a recommendation
     * @param processTime the processing time for the system to handle the user request
     */
    public static void sendToMongoError(MongoCollection<Document> collection, String username, Timestamp timestamp, String
            responseGenerated, long processTime) {
        // create a new document
        Document doc = new Document("name", username);
        doc.append("timestamp", String.valueOf(timestamp));
        doc.append("response_generated", responseGenerated);
        doc.append("process_time", String.valueOf(processTime));
        // store data into collection
        collection.insertOne(doc);
        System.out.println("error doc inserted");
    }

    /***
     * generateDashboard --- performs the calculations and process the data on MongoDB.
     * Three maps will be updated (which are later used for the top statistics) and a log table string will be created.
     * @param collection the location where all documents are stored
     * @return tableValues: a String builder which contains all log information to be displayed on the dashboard
     */
    public static String generateDashboard(MongoCollection<Document> collection) {
        // reset previous records
        users.clear();
        requestDates.clear();
        responseGenerateSuccess.clear();
        types.clear();
        processTimes.clear();
        tableValues.setLength(0);

        // iterate through all documents in the collection
        FindIterable<Document> iterDoc = collection.find();
        for (Document document : iterDoc) {
            // get the data from each document we need for the statistics
            String doc_username = document.getString("name");
            String doc_timestamp = document.getString("timestamp");
            // extract the date from timestamp
            String doc_date = doc_timestamp.substring(0,10);
            String doc_response_generated = document.getString("response_generated");
            String doc_processTime = document.getString("process_time");

            // to store if the response generated success is yes
            String doc_activity = "";
            String doc_type = "";
            String doc_participants = "";
            String doc_price = "";

            // if the document does contain activity, type, participants, price
            if (doc_response_generated.equals("Yes")) {
                doc_activity = document.getString("activity");
                doc_type = document.getString("type");
                doc_participants = document.getString("num_of_participants");
                doc_price = document.getString("estimated_price");
            }

            // for usernames map
            // if this username is already in the map
            if (users.containsKey(doc_username)) {
                users.put(doc_username, users.get(doc_username) + 1);
            } else {
                // put this username into the users map
                users.put(doc_username, 1);
            }

            // for request dates map
            // if this username is already in the map
            if (requestDates.containsKey(doc_date)) {
                requestDates.put(doc_date, requestDates.get(doc_date) + 1);
            } else {
                // put this timestamp into the users map
                requestDates.put(doc_date, 1);
            }

            // for response generate successful rates map
            // if this successful status is already in the map
            if (responseGenerateSuccess.containsKey(doc_response_generated)) {
                responseGenerateSuccess.put(doc_response_generated, responseGenerateSuccess.get(doc_response_generated) + 1);
            } else {
                // put this status into the response generate success map
                responseGenerateSuccess.put(doc_response_generated, 1);
            }

            // for activity types:
            // if this document contains type data (for errors, no type data will be sent to MongoDB)
            if (doc_type != null) {
                // if this type is already in the map
                if (types.containsKey(doc_type)) {
                    types.put(doc_type, types.get(doc_type) + 1);
                } else {
                    // put this username into the users map
                    types.put(doc_type, 1);
                }
            }

            // for process times map
            // if this process time is already in the map
            if (processTimes.containsKey(doc_processTime)) {
                processTimes.put(doc_processTime, processTimes.get(doc_processTime) + 1);
            } else {
                // put this status into the response generate success map
                processTimes.put(doc_processTime, 1);
            }

            // create the log table
            tableValues.append("<tr>");
            tableValues.append("<td>").append(doc_username).append("</td>");
            tableValues.append("<td>").append(doc_timestamp).append("</td>");
            tableValues.append("<td>").append(doc_response_generated).append("</td>");
            tableValues.append("<td>").append(doc_activity).append("</td>");
            tableValues.append("<td>").append(doc_type).append("</td>");
            tableValues.append("<td>").append(doc_participants).append("</td>");
            tableValues.append("<td>").append(doc_price).append("</td>");
            tableValues.append("<td>").append(doc_processTime).append("</td>");
            tableValues.append("</tr>");
        }
        return tableValues.toString();
    }

    /***
     * getTopUser --- get the list of top users in the users map
     * If the users map is empty, return -1 as the user count.
     *
     * code referenced from https://www.geeksforgeeks.org/traverse-through-a-hashmap-in-java/
     * @return the top users map
     */
    public Map<Integer, List<String>> getTopUser() {
        // create an array list to store the top users
        List<String> topUsers = new ArrayList<>();
        Integer currentMax = -1;
        // Looping through the users map with for-each loop
        for (Map.Entry<String, Integer> mapElement : users.entrySet()) {
            if (mapElement.getValue() > currentMax) {
                topUsers = new ArrayList<>();
                topUsers.add(mapElement.getKey());
                currentMax = mapElement.getValue();
            } else if (mapElement.getValue() == currentMax) {
                topUsers.add(mapElement.getKey());
            }
        }

        // create a top users map and store the maximum counts & top users
        Map<Integer, List<String>> maxUsers = new HashMap<>();
        maxUsers.put(currentMax, topUsers);
        return maxUsers;
    }

    /***
     * getTopRequestDates --- get the top request dates in the dates map
     * If request dates map is empty, return -1 as the date count
     *
     * code referenced from https://www.geeksforgeeks.org/traverse-through-a-hashmap-in-java/
     * @return the top request dates map
     */
    public Map<Integer, List<String>> getTopRequestDates() {
        // create an array list to store the top request dates
        List<String> topRequestDates = new ArrayList<>();
        Integer currentMax = -1;
        // Looping through the request dates map with for-each loop
        for (Map.Entry<String, Integer> mapElement : requestDates.entrySet()) {
            if (mapElement.getValue() > currentMax) {
                topRequestDates = new ArrayList<>();
                topRequestDates.add(mapElement.getKey());
                currentMax = mapElement.getValue();
            } else if (mapElement.getValue() == currentMax) {
                topRequestDates.add(mapElement.getKey());
            }
        }

        // create a top dates map and store the maximum counts & top dates
        Map<Integer, List<String>> maxRequestDates = new HashMap<>();
        maxRequestDates.put(currentMax, topRequestDates);
        return maxRequestDates;
    }

    // if successful rate is -1, meaning that the denominator is 0 / generate success map is empty

    /***
     * getResponseSuccessfulRate --- calculate the rate of how many requests have been completed with response generated
     * if response generate success status map is empty, return -1
     * @return the response successful rate (* 100 as the dashboard displayes percentage)
     */
    public Double getResponseSuccessfulRate(){
        Double successfulRate = -1.0;
        // get total counts in the response success map
        if (!responseGenerateSuccess.isEmpty()) {
            Integer counts = 0;
            for (Integer value: responseGenerateSuccess.values()){
                counts += value;
            }

            // calculate successful rate
            if (responseGenerateSuccess.get("Yes") != 0) {
                successfulRate = responseGenerateSuccess.get("Yes") / Double.valueOf(counts);
            } else {
                // if no requests are successful, set the rate to 0
                successfulRate = 0.0;
            }
        }
        return successfulRate * 100;
    }

    // code referenced from https://www.geeksforgeeks.org/traverse-through-a-hashmap-in-java/
    /***
     * getTopActivityType --- get the top activity types in the types map
     * If types map is empty, return -1 as the date count
     *
     * code referenced from https://www.geeksforgeeks.org/traverse-through-a-hashmap-in-java/
     * @return the top types map
     */
    public Map<Integer, List<String>> getTopActivityType() {
        // create an array list to store the top activities
        List<String> topActivities = new ArrayList<>();
        Integer currentMax = -1;
        // Looping through the types map with for-each loop
        for (Map.Entry<String, Integer> mapElement : types.entrySet()) {
            if (mapElement.getValue() > currentMax) {
                topActivities = new ArrayList<>();
                topActivities.add(mapElement.getKey());
                currentMax = mapElement.getValue();
            } else if (mapElement.getValue() == currentMax) {
                topActivities.add(mapElement.getKey());
            }
        }

        // create a top types map and store the maximum counts & top types
        Map<Integer, List<String>> maxActivityTypes = new HashMap<>();
        maxActivityTypes.put(currentMax, topActivities);
        return maxActivityTypes;
    }

    /**
     * getAverageProcessTime --- loops through the process time map and calculates the average process time
     * if process times map is empty, return -1
     * @return average process time
     */
    public long getAverageProcessTime() {
        long averageProcessTime = -1;
        long totalProcessTime = -1;
        Integer counts = 0;
        // Looping through the processTimes map with for-each loop
        for (Map.Entry<String, Integer> mapElement : processTimes.entrySet()) {
            totalProcessTime += Long.parseLong(mapElement.getKey());
            counts += mapElement.getValue();
        }

        // calculate average process time by dividing total process time by counts
        try {
           averageProcessTime = Math.round(totalProcessTime / counts);
        } catch (ArithmeticException e) {
            System.out.println("Cannot calculate average processing time");
        }
        return averageProcessTime;
    }

}


