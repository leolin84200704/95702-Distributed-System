import java.net.*;
import java.io.*;
import java.util.Scanner;

// Name: Leo Lin
// Andrew ID: hungfanl

public class EavesdropperUDP {

    public static void main(String args[]){
        // Showing that the Eavesdropper is running
        System.out.println("The Eavesdropper is running.");

        try {
            // args give message contents and server hostname
            InetAddress aHost = InetAddress.getByName("localhost");
            // set up two new socket
            DatagramSocket aSocket = null;
            DatagramSocket bSocket = null;
            Scanner readInput = new Scanner(System.in);
            // The dropper will listen to port number 6798
            System.out.println("Please insert the port to listen on");
            int listenPort = readInput.nextInt();
            // The dropper will pass the message to 6789
            System.out.println("Please insert the port number to masquerade");

            int masqueradePort = readInput.nextInt();
            byte[] buffer = new byte[1000];
            // The listen port will listen to port 6798
            aSocket = new DatagramSocket(listenPort);
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            bSocket = new DatagramSocket();
            // Transfer the user's input to byte form and transfer to the server
            while (true) {
                // aSocket receives a request from the client
                aSocket.receive(request);
                // Extract the string and print out to the console
                // if the client make a halt request, the eavesdropper will notice
                String requestString = new String(request.getData()).substring(0,request.getLength());
                if(requestString.equals("halt!")) {
                    System.out.println("********************");
                    System.out.println("A halt message has arrived and is being forwarded to the server");
                    System.out.println("********************");
                }
                else System.out.println("Client: "+requestString);
                // Sent the client's request to the server
                DatagramPacket myRequest = new DatagramPacket(request.getData(), request.getLength(), aHost, masqueradePort);
                bSocket.send(myRequest);
                DatagramPacket replyMessage = new DatagramPacket(buffer, buffer.length);
                // receive the reply from the server side and sends it back to the .
                bSocket.receive(replyMessage);
                String replyString = new String(replyMessage.getData()).substring(0, replyMessage.getLength());
                // If replyString is "halt!" break the while loop
                if(!replyString.equals("halt!")) {
                    System.out.println("Server: " + replyString);
                }
                DatagramPacket reply = new DatagramPacket(replyMessage.getData(),
                        replyMessage.getLength(), request.getAddress(), request.getPort());
                aSocket.send(reply);
            }

        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){System.out.println("IO: " + e.getMessage());
        }
    }
}