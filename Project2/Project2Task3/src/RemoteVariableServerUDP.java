import java.net.*;
        import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
// Name: Leo Lin
// Andrew ID: hungfanl

public class RemoteVariableServerUDP{

    static Map<Integer, Integer> database = new TreeMap<>();
    public static void main(String args[]){
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
                // create a DatagramPacket object with the request
                String requestString = new String(request.getData()).substring(0,request.getLength());
                // split the passed string into operator, id, and number.
                String[] operation = requestString.split(",");
                int operator = Integer.parseInt(operation[0]);
                int id = Integer.parseInt(operation[1]);
                int number = Integer.parseInt(operation[2]);
                // conduct corresponding arithmetic according to the three index
                int outcome = arithmetic(operator, id, number);
                // return the outcome of calculation to the client
                byte [] m = String.valueOf(outcome).getBytes();
                DatagramPacket reply = new DatagramPacket(m,
                        m.length, request.getAddress(), request.getPort());
                // send the reply back to the client
                System.out.println("Returning sum of " + outcome + " to client");
                aSocket.send(reply);
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }


    // Doing corresponding arithmetic according to the user's request
    public static int arithmetic(int operator, int id, int number){
        String[] operation = {"Addition", "Subtraction", "Check"};
        System.out.println("The visitor's ID is: " + id);
        System.out.println("Operand: " + operation[operator-1]);
        if(operator == 1){
            database.put(id, database.getOrDefault(id,0) + number);
        }
        else if (operator == 2){
            database.put(id, database.getOrDefault(id,0) - number);
        }
        // return the number after calculation
        return database.get(id);
    }
}