import java.net.*;
import java.io.*;
import java.util.Scanner;
// Name: Leo Lin
// Andrew ID: hungfanl
public class AddingClientUDP{
    static DatagramSocket aSocket = null;
    static int serverPort;
    static InetAddress aHost;
    public static void main(String args[]){
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
        String s;
        System.out.println();
        do{
            s = readInput.nextLine();
            // If the client does not input "halt!" initiate the add function
            if(!s.equals("halt!")){
                int result = add(Integer.parseInt(s));
                System.out.println("The server returned " + result +".");
            }
        }while(!s.equals("halt!"));

        System.out.println("Client side quitting.");
        if(aSocket != null) aSocket.close();


    }

    //This function takes a integer from the user, pass it to the server, and return the respond of the server
    public static int add(int i){
        String replyString = null;
        try {
            byte [] m = String.valueOf(i).getBytes();
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