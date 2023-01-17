import java.rmi.*;
import java.math.BigInteger;
public interface Bumper extends Remote {
    // this method will be called from remote clients
    boolean bump () throws RemoteException;
    BigInteger get() throws RemoteException;
}