//*******************************************************
// BumperClient.java
// This client gets a remote reference from the rmiregistry

import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.util.StringTokenizer;
import java.math.BigInteger;


public class BumperClient {
   public static void main(String args[]) throws Exception {
        BufferedReader in  =
                     new BufferedReader(
                         new InputStreamReader(System.in));
        // connect to the rmiregistry and get a remote reference to the Bumper
        // object.
        Bumper b  = (Bumper) Naming.lookup("//localhost/CoolBumper");
 	    System.out.println("Found Bumper. Start Counting");
        BigInteger ctr = new BigInteger("0");
        BigInteger n = new BigInteger("10000");

        long start = System.currentTimeMillis();
       	while(!ctr.equals(n)) {
            try {
                    b.bump();
                    ctr = ctr.add(new BigInteger("1"));
            }
    	    catch(RemoteException e) {
                System.out.println("allComments: " + e.getMessage());
            }
    	}
        long stop = System.currentTimeMillis();
        System.out.println("Held = " + b.get().toString());
        System.out.println("Total time: " + (stop - start) + " milliseconds");
    }
}