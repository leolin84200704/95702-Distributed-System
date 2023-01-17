//*************************************************************
// CalculatorServant.java
// A Remote object class that implements Calculator.
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.math.BigInteger;
public class BumperServant extends UnicastRemoteObject implements Bumper {
       public BumperServant() throws RemoteException {
       }
       public static BigInteger held = BigInteger.valueOf(0);
       public boolean bump() throws RemoteException{
          held = held.add(new BigInteger("1"));
          // if(new BigInteger(held.remainder("100")).equals(new BigInteger("0"))) System.out.println("Held is now " + held);
          return true;
       }
       public BigInteger get() throws RemoteException{
          System.out.print("Return held.");
          return held;
       }
}