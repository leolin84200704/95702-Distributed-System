package cmu.edu.ds;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

/*
Based on Coulouris UDP socket code
 */
public class UDPServer {
    private DatagramSocket socket = null;
    private InetAddress inetAddress = null;
    private int port;
    static BigInteger p = new BigInteger("6231720984236661927862601680191594334327223260139907210971108379566305783259160955632448191195213287");
    static BigInteger g = new BigInteger("5");

    public static void main(String[] args) {
        UDPServer udpServer = new UDPServer();
        udpServer.init(7272);
        Random rnd = new Random();
        BigInteger secret = new BigInteger(2046, rnd);
        System.out.println("secret number = " + secret);
        BigInteger pass = g.modPow(secret,p);
        BigInteger receive = new BigInteger(udpServer.receive());
        System.out.println("passing = " + pass);
        udpServer.send(pass.toString(10));
        BigInteger answer = receive.modPow(secret,p);
        System.out.println("Answer: " + answer);

        udpServer.close();
    }

    private void init(int portnumber) {
        try {
            socket = new DatagramSocket(portnumber);
            System.out.println("Server socket created");
        } catch (SocketException e) {
            System.out.println("Socket error " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error " + e.getMessage());
        }
    }

    private void send(String message) {
        byte[] buffer = new byte[256];
        buffer = message.getBytes();
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length, inetAddress, port);
        try {
            socket.send(reply);
        } catch (SocketException e) {
            System.out.println("Socket error " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error " + e.getMessage());
        }

    }

    private String receive() {
        byte[] buffer = new byte[256];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(request);
            inetAddress = request.getAddress();
            port = request.getPort();
        } catch (SocketException e) {
            System.out.println("Socket error " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error " + e.getMessage());
        }
        return new String(request.getData(), 0, request.getLength());
    }

    private void close() {
        if (socket != null) socket.close();
    }
}

