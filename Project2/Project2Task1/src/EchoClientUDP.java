import java.net.*;
import java.io.*;
import java.util.Scanner;
// Name: Leo Lin
// Andrew ID: hungfanl
public class EchoClientUDP{
    public static void main(String args[]){
        // Showing that the client UDP is running
        System.out.println("The client is running.");
        // args give message contents and server hostname
        DatagramSocket aSocket = null;
        try {
            // collecting IP address
            InetAddress aHost = InetAddress.getByName("localhost");
            // The client is using 6789 as its port number
            Scanner readInput = new Scanner(System.in);
            System.out.println("Please insert the server side port number");
            int serverPort = readInput.nextInt();
            // set up a new socket
            aSocket = new DatagramSocket();
            String nextLine;
            // Initiate a BufferedReader that record the user's input
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            // Transfer the user's input to byte form and transfer to the server
            while ((nextLine = typed.readLine()) != null) {
                // get byte form and turn into a DatagramPacket file
                byte [] m = nextLine.getBytes();
                DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
                // send the request
                aSocket.send(request);

                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                // receive the reply from the server side and print it out.
                aSocket.receive(reply);
                String replyString = new String(reply.getData()).substring(0, reply.getLength());
                // If replyString is "halt!" break the while loop
                if(replyString.equals("halt!")) {
                    System.out.println("Client side quitting");
                    break;
                }
                System.out.println("Reply: " + replyString);
            }

        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){System.out.println("IO: " + e.getMessage());
        }finally {
            if(aSocket != null) aSocket.close();
        }
    }
}