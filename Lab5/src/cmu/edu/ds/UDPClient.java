package cmu.edu.ds;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*
Based on Coulouris UDP socket code
 */
public class UDPClient {
    private DatagramSocket socket = null;
    private InetAddress host = null;
    private int port;
    static BigInteger p = new BigInteger("6231720984236661927862601680191594334327223260139907210971108379566305783259160955632448191195213287");
    static BigInteger g = new BigInteger("5");

    public static void main(String[] args) {
        UDPClient udpClient = new UDPClient();
        Random rnd = new Random();
        BigInteger secret = new BigInteger(2046, rnd);
        System.out.println("secret number = " + secret);
        udpClient.init("localhost", 7272);
        BigInteger pass = g.modPow(secret,p);
        System.out.println("passing = " + pass);
        udpClient.send(pass.toString(10));
        BigInteger receive = new BigInteger(udpClient.receive());
        BigInteger answer = receive.modPow(secret,p);
        System.out.println("Answer: " + answer);
        udpClient.close();
    }

    private void init(String hostname, int portNumber) {
        try {
            host = InetAddress.getByName(hostname);
            port = portNumber;
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Socket error " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error " + e.getMessage());
        }
    }

    private void send(String message) {
        byte[] m = message.getBytes();
        DatagramPacket packet = new DatagramPacket(m, m.length, host, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.out.println("IO error " + e.getMessage());
        }
    }

    private String receive() {
        byte[] answer = new byte[256];
        DatagramPacket reply = new DatagramPacket(answer, answer.length);
        try {
            socket.receive(reply);
        } catch (IOException e) {
            System.out.println("IO error " + e.getMessage());
        }
        return(new String(reply.getData(), 0, reply.getLength()));

    }

    private void close() {
        if (socket != null) socket.close();
    }
}
