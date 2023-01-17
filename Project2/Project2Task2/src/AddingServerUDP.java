import java.net.*;
import java.io.*;
import java.util.Scanner;
// Name: Leo Lin
// Andrew ID: hungfanl

public class AddingServerUDP{
    static int sum;
    public static void main(String args[]){
        sum = 0;
        DatagramSocket aSocket = null;
        byte[] buffer = new byte[1000];
        System.out.println("Server started.");
        int listenPort = 6789;
        try{
            aSocket = new DatagramSocket(listenPort);
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            // Whenever the aSocket receive a request from the client side, it will add it to the sum variable
            // and return the sum value. It will then print out the sum
            while(true){
                // receive a request from the client
                aSocket.receive(request);
                // Create a DatagramPacket object with the request
                String requestString = new String(request.getData()).substring(0,request.getLength());
                // add the integer from the client and add it to the sum variable
                add(Integer.parseInt(requestString));
                byte [] m = String.valueOf(sum).getBytes();
                DatagramPacket reply = new DatagramPacket(m,
                        m.length, request.getAddress(), request.getPort());

                // send the reply back to the client
                System.out.println("Returning sum of " + sum + " to client");
                aSocket.send(reply);
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }

    public static void add(int number){
        System.out.println("Adding: " + number + " to " + sum);
        sum += number;
    }
}