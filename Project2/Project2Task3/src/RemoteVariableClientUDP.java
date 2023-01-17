
import java.net.*;
import java.io.*;
import java.util.Scanner;

// Name: Leo Lin
// Andrew ID: hungfanl
public class RemoteVariableClientUDP{
    static DatagramSocket aSocket = null;
    static int serverPort;
    static InetAddress aHost;
    public static void main(String aƒƒrgs[]){
        // Showing that the client UDP is running
        Scanner readInput = new Scanner(System.in);
        System.out.println("The client is running.");
        // get the port number from the client
        System.out.println("Please enter server port:");
        serverPort = Integer.parseInt(readInput.nextLine());
        try {
            // collecting IP address
            aHost = InetAddress.getByName("localhost");
            aSocket = new DatagramSocket();
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){System.out.println("IO: " + e.getMessage());
        }
        String option;
        System.out.println();
        // Keep on looping until the user insert option 4
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
        if(aSocket != null) aSocket.close();


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
        String replyString = null;
        try {
            byte [] m = String.valueOf(s).getBytes();
            DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
            // send the request
            aSocket.send(request);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            // receive the reply from the server side and print it out.
            aSocket.receive(reply);
            replyString = new String(reply.getData()).substring(0, reply.getLength());
            // If replyString is "halt!" break the while loop
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){System.out.println("IO: " + e.getMessage());
        }
        return Integer.parseInt(replyString);
    }
}