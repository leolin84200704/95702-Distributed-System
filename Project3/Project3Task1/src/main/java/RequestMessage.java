// Refer to https://www.javatpoint.com/java-json-example
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.*;
import java.io.*;
import java.util.Scanner;
// Name: Leo Lin
// AndrewID: hungfanl

public class RequestMessage {
    static Socket clientSocket;
    static int serverPort = 7777;
    static BufferedReader in;
    static PrintWriter out;
    static Scanner readInput = new Scanner(System.in);
    static boolean finish = false;
    static JSONObject json = new JSONObject();
    public static void main(String args[]) {
        try{
            clientSocket = new Socket("localhost", serverPort);
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                // settle the json string
                int option = getSelection();
                if(finish) break;
                // pass the json to the server
                pass(option);
            }

        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            try {
                if(clientSocket != null) clientSocket.close();
            } catch (IOException e) {

            }
        }
    }

    // Allows the user to choose from different operations and collect necessary information.
    public static int getSelection() {
        String[] message = {"View basic blockchain status.", "Add a transaction to the blockchain.",
                "Verify the blockchain.", "View the blockchain.", "Corrupt the chain.",
                "Hide the corruption by repairing the chain.", "Exit."};
        json.clear();
        for(int i = 0; i < message.length; i++) {
            System.out.println(i + ". " + message[i]);
        }
        int option = Integer.parseInt(readInput.nextLine());
        switch (option) {
            case 0:
                json.put("selection", 0);
                break;
            case 1:
                System.out.println("Enter difficulty > 0");
                int difficulty = Integer.parseInt(readInput.nextLine());
                System.out.println("Enter transaction");
                String data = readInput.nextLine();
                json.put("selection", 1);
                json.put("difficulty", difficulty);
                json.put("data", data);
                break;
            case 2:
                json.put("selection", 2);
                break;
            case 3:
                json.put("selection", 3);
                break;
            case 4:
                System.out.println("Enter block ID of block to corrupt");
                int index = Integer.parseInt(readInput.nextLine());
                System.out.println("Enter new data for block " + index);
                String corrupt_message = readInput.nextLine();
                json.put("selection", 4);
                json.put("index", index);
                json.put("data", corrupt_message);
                break;
            case 5:
                json.put("selection", 5);
                break;
            case 6:
                finish = true;
                break;
            default:
                break;
        }
        return option;
    }

    // Pass the information to the server
    public static void pass(int option){
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            out.println(json.toJSONString());
            out.flush();
            json = (JSONObject) JSONValue.parse(in.readLine()); // read a line of data from the stream
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        }
        switch (option) {
            case 0:
                System.out.println("Current size of chain: " + ((Long) json.get("size")).intValue());
                System.out.println("Difficulty of most recent block: " + ((Long) json.get("diff")).intValue());
                System.out.println("Total difficulty for all blocks: " + ((Long) json.get("totalDiff")).intValue());
                System.out.println("Approximate hashes per second on this machine: " + ((Long) json.get("hps")).intValue());
                System.out.println("Expected total hashes required for the whole chain: " + (double) json.get("totalHashes"));
                System.out.println("Nonce for most recent block: " + ((Long) json.get("recentNonce")).intValue());
                System.out.println("Chain hash: " + json.get("chainHash"));
                break;
            case 1:
                System.out.println(json.get("response"));
                break;
            case 2:
                System.out.println("Chain verification: " + json.get("verification"));
                if(json.get("verification").equals("False")) System.out.println(json.get("errorMessage"));
                System.out.println(json.get("response"));
                break;
            case 3:
                // Intentionally not break;
                System.out.println("View the Blockchain");
                System.out.println(json.get("response"));
                break;
            case 4:
                System.out.println("corrupt the Blockchain");
            case 5:
                System.out.println(json.get("response"));
                break;
        }
    }
}
