import java.net.*;
import java.io.*;
import java.util.Scanner;
// Name: Leo Lin
// Andrew ID: hungfanl
public class RemoteVariableClientTCP {
    static Socket clientSocket = null;
    static int serverPort;
    static BufferedReader in;
    static PrintWriter out;
    public static void main(String args[]) {
        Scanner readInput = new Scanner(System.in);
        // arguments supply hostname
        System.out.println("The client is running.");
        // get the port number from the client
        System.out.println("Please enter server port:");
        serverPort = readInput.nextInt();
        try {
            clientSocket = new Socket("localhost", serverPort);
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            String option;
            System.out.println();
            do{
                // Initiate the getOption function
                option = getOption();
                // If user doesn't insert 4, then pass function will pass the string to the server
                if(!option.equals("4")){
                    int result = pass(option);
                    System.out.println("The result is " + result +".");
                    System.out.println();
                }
            }while(!option.equals("4"));
            System.out.println("Client side quitting. The remote variable server is still running.");
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }
    // This function returns a string concatenating type, id, number
    // And returns 4 if the user insert 4.
    public static String getOption(){
        Scanner readInput = new Scanner(System.in);
        String[] options = {"Add a value to your sum.","Subtract a value from your sum.","Get your sum.","Exit client."};
        for(int i = 0; i < options.length; i++){
            System.out.println((i+1) + ". " + options[i]);
        }
        int choice = Integer.parseInt(readInput.nextLine());
        String number = "0";
        String id;
        // return string based on user's insertion.
        switch(choice) {
            // if the user insert 4, then return 4 to exit the client program
            case 4:
                return String.valueOf(choice);
            case 1:
                System.out.println("Enter value to add:");
                number = readInput.nextLine();
                break;
            case 2:
                System.out.println("Enter value to subtract:");
                number = readInput.nextLine();
                break;
            default:
        }
        System.out.println("Enter your ID:");
        id = readInput.nextLine();
        return String.valueOf(choice) + "," + id + "," + number;
    }
    //This function takes the concatenated string from the client and pass it to the server
    public static int pass(String s){
        String data = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            out.println(s);
            out.flush();
            data = in.readLine(); // read a line of data from the stream
            System.out.println("Received: " + data);
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        }
        return  Integer.parseInt(data);
    }
}

