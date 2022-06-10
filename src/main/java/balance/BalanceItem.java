package balance;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BalanceItem extends Remote {
	int getWeight() throws RemoteException;
}
