import java.net.*;
import java.io.*;
import java.util.Scanner;
// Name: Leo Lin
// Andrew ID: hungfanl

public class EchoServerUDP{
    public static void main(String args[]){
        DatagramSocket aSocket = null;
        byte[] buffer = new byte[1000];
        System.out.println("The server is running.");
        Scanner readInput = new Scanner(System.in);
        System.out.println("Please insert the port to listen");
        int listenPort = readInput.nextInt();
        try{
            aSocket = new DatagramSocket(listenPort);
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            // Whenever the aSocket receive a request from the client side, it will document its data, length, address
            // and port into "reply". It will then print out the request string and send a reply back
            while(true){
                // receive a request from the client
                aSocket.receive(request);
                // Create a DatagramPacket object with the request
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                String requestString = new String(request.getData()).substring(0,request.getLength());
                // send the reply back to the client
                aSocket.send(reply);
                if(requestString.equals("halt!")){
                    System.out.println("Server side quitting");
                    break;
                }
                // print out the request in a string form from byte form
                System.out.println("Echoing: "+requestString);
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }
}