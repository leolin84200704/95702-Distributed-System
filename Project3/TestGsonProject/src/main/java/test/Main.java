package test;
import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) {
        // Create a message
        Message msg = new Message("Alice", 30);
        // Create a Gson object
        Gson gson = new Gson();
        // Serialize to JSON
        String messageToSend = gson.toJson(msg);
        // Display the JSON string
        System.out.println(messageToSend);

        // Suppose we receive the following JSON string from a network or file.
        // Double quotes would be used in a real message. Single quotes are used
        // here because we are doing this within a Java program.
        String someJSON = "{'id':45,'name':'Bob'}";
        Message incommingMsg = gson.fromJson(someJSON,Message.class);
        System.out.println(incommingMsg.name);
        System.out.println(incommingMsg.id);

    }
}